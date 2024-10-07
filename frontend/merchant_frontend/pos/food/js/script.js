let lpnumber = null;
const url = "https://merchant.mobipay.kr/api/v1";
const MERCHANT_TYPE = 'FOOD';
const MER_API_KEY = 'Da19J03F6g7H8iB2c54e';

async function loadDatabase() {
  const db = await idb.openDB("mobi_merchant_fnb", 1, {
    upgrade(db, oldVersion, newVersion, transaction) {
      db.createObjectStore("products", {
        keyPath: "id",
        autoIncrement: true,
      });
      db.createObjectStore("sales", {
        keyPath: "id",
        autoIncrement: true,
      });
    },
  });

  return {
    db,
    getProducts: async () => await db.getAll("products"),
    addProduct: async (product) => await db.add("products", product),
    editProduct: async (product) => await db.put("products", product),
    deleteProduct: async (product) => await db.delete("products", product.id),
    clearProducts: async () => {
      const tx = db.transaction("products", "readwrite");
      await tx.objectStore("products").clear();
      await tx.done;
    },
  };
}


function initApp() {
  const app = {
    db: null,
    activeMenu: 'pos',
    products: [],
    keyword: "",
    cart: [],
    isShowModalReceipt: false,
    isShowModalSuccess: false,
    isLoading: false,
    receiptNo: null,
    receiptDate: null,
    lpno: null,
    isMobiUser: false,
    car_present: false,
    model: null,
    video: null,

    initVideo() {
      this.video = document.getElementById('video');
    },

    async initDatabase() {
      this.db = await loadDatabase();
      await this.loadJsonData();
    },


    async loadJsonData() {
      await this.db.clearProducts();

      const response = await fetch("db.json");
      const data = await response.json();
      this.products = data.products;

      for (let product of data.products) {
        await this.db.addProduct(product);
      }
    },

    getProducts() {
      return this.products;
    },

    addToCart(product) {
      const index = this.findCartIndex(product);
      if (index === -1) {
        this.cart.push({
          productId: product.id,
          image: product.image,
          name: product.name,
          price: product.price,
          option: product.option,
          qty: 1,
        });
      } else {
        this.cart[index].qty += 1;
      }
      this.beep();
    },

    findCartIndex(product) {
      return this.cart.findIndex((p) => p.productId === product.id);
    },

    addQty(item, qty) {
      const index = this.cart.findIndex((i) => i.productId === item.productId);
      if (index === -1) {
        return;
      }
      const afterAdd = item.qty + qty;
      if (afterAdd === 0) {
        this.cart.splice(index, 1);
        this.clearSound();
      } else {
        this.cart[index].qty = afterAdd;
        this.beep();
      }
    },

    getItemsCount() {
      return this.cart.reduce((count, item) => count + item.qty, 0);
    },

    getTotalPrice() {
      return this.cart.reduce(
          (total, item) => total + item.qty * item.price,
          0
      );
    },

    submit() {
      const time = new Date();
      this.isShowModalReceipt = true;
      this.receiptNo = `MOBIPOS-${Math.round(time.getTime() / 1000)}`;
      this.receiptDate = this.dateFormat(time);
    },

    submitable() {
      return this.getTotalPrice() > 0;
    },

    closeModalReceipt() {
      this.isShowModalReceipt = false;
    },

    dateFormat(date) {
      const formatter = new Intl.DateTimeFormat('id', { dateStyle: 'short', timeStyle: 'short'});
      return formatter.format(date);
    },

    numberFormat(number) {
      return (number || "")
          .toString()
          .replace(/^0|\./g, "")
          .replace(/(\d)(?=(\d{3})+(?!\d))/g, "$1,");
    },

    priceFormat(number) {
      return number ? `${this.numberFormat(number)} 원` : `0 원`;
    },

    clear() {
      this.cart = [];
      this.receiptNo = null;
      this.receiptDate = null;
      this.isShowModalReceipt = false;
      this.isMobiUser = false;
      this.lpno = null;
      this.car_present = false;
      this.clearSound();
    },

    beep() {
      this.playSound("sound/beep.mp3");
    },

    clearSound() {
      this.playSound("sound/clear.mp3");
    },

    playSound(src) {
      const sound = new Audio();
      sound.src = src;
      sound.play();
      sound.onended = () => delete(sound);
    },



    requestPayMobi() {
      this.closeModalReceipt();
      this.isLoading = true;

      // websocket 연결
      const socket = new WebSocket('wss://merchant.mobipay.kr/api/v1/merchants/websocket');

      let sessionId; // 세션 ID를 저장할 변수


      socket.onopen = async (event) => {
        console.log('WebSocket is open now.');

        let info = this.cart.map(item => `${item.name} x ${item.qty}`).join(', ');
        let paymentBalance = this.getTotalPrice();
        let carNumber = this.lpno || "번호 인식 실패";

        if(carNumber == null) {
          console.log("차량번호 없음");
           return;
        }

        // 결제 요청
        const paymentRequest = {
          "type": MERCHANT_TYPE, // 가맹점 종류
          "paymentBalance": paymentBalance,
          "carNumber": carNumber,
          "info": info // 결제 정보
        };

        try {
          const response = await postRequest('/merchants/payments/request', paymentRequest);
          console.log('결제 요청 성공, 결제 결과 대기 중...');
        } catch (error) {
          console.error('결제 요청 실패:', error);
          // 웹소켓 연결 해제
          socket.close();
          alert('결제 요청 실패');
        }
      };

      socket.onclose = (event) => {
        console.log('WebSocket is closed now.');
      };

      socket.onerror = (error) => {
        console.log('WebSocket error:', error);
      };

      socket.onmessage = (event) => {
        const message = JSON.parse(event.data);

        if (message.sessionId) {
          sessionId = message.sessionId;
          socket.send(JSON.stringify({ "type": MERCHANT_TYPE }));
        } else {
          if (message.success) {
            this.isLoading = false;
            this.isShowModalSuccess = true;
          } else {
            this.isLoading = false;
            alert('결제 실패');
          }
          socket.close();
        }
      };

    },

    startCamera(facingMode) {
      return navigator.mediaDevices.getUserMedia({
        video: {
          facingMode: facingMode,
          width: { ideal: 1920 },
          height: { ideal: 1080 },
          frameRate: { ideal: 60 }
        }
      });
    },

    async detectObjects() {
      try {
        const predictions = await this.model.detect(this.video);
        this.car_present = false;

        predictions.forEach((prediction) => {
          if (prediction.class === 'car') {
            this.car_present = true;

            const [x, y, width, height] = prediction.bbox;

            if (height > 240 && width > 350) {
              const startTime = performance.now();
              const canvas = document.createElement('canvas');
              canvas.width = width;
              canvas.height = height;
              const context = canvas.getContext('2d');

              context.drawImage(this.video, x, y, width, height, 0, 0, width, height);

              const self = this;

              canvas.toBlob(async (blob) => {
                const formData = new FormData();
                formData.append('file', blob, 'image.jpg');
                const endTime = performance.now();
                const duration = endTime - startTime;
                console.log("변환 시간: " + duration.toFixed(3) + "ms");

                try {
                  const response = await fetch('https://anpr.mobipay.kr/predict/', {
                    method: 'POST',
                    body: formData,
                  });

                  const data = await response.json();
                  if (data !== null) {
                    const confidence = parseFloat(data.confidence);

                    if (confidence > 0.85) {
                      self.$data.lpno = data.predicted_text;
                      self.$data.isMobiUser = true;
                    } else {
                      console.log("정확도 낮음");
                      self.$data.lpno = null;
                      self.$data.isMobiUser = false;
                    }
                  }
                } catch (error) {
                  console.error('POST 실패: ', error);
                }
              });
            }
          }
        });

        if (!this.car_present) {
          // 차량 감지 안 된 경우 처리
        }

        setTimeout(() => {
          requestAnimationFrame(() => this.detectObjects());
        }, 600);
      } catch (error) {
        console.error('물체 감지 실패: ', error);

        setTimeout(() => {
          requestAnimationFrame(() => this.detectObjects());
        }, 600);
      }
    },

    initANPR(){
      if (navigator.mediaDevices && navigator.mediaDevices.getUserMedia) {
        this.startCamera({ exact: 'environment' })
            .then((stream) => {
              this.video.srcObject = stream;
              this.video.play();
              const track = stream.getVideoTracks()[0];
              const settings = track.getSettings();
              this.video.width = settings.width;
              this.video.height = settings.height;

              cocoSsd.load().then((loadedModel) => {
                this.model = loadedModel;
                this.detectObjects();
              }).catch((error) => {
                console.error('모델 로드 실패: ', error);
              });
            })
            .catch((error) => {
              console.warn('후면카메라 사용 불가하여 전면 카메라를 사용합니다.');
              this.startCamera('user')
                  .then((stream) => {
                    this.video.srcObject = stream;
                    this.video.play();
                    const track = stream.getVideoTracks()[0];
                    const settings = track.getSettings();
                    this.video.width = settings.width;
                    this.video.height = settings.height;

                    cocoSsd.load().then((loadedModel) => {
                      this.model = loadedModel;
                      this.detectObjects();
                    }).catch((error) => {
                      console.error('모델 로드 실패: ', error);
                    });
                  })
                  .catch((error) => {
                    console.error('전면카메라 사용 불가: ', error);
                  });
            });
      } else {
        console.error('getUserMedia 사용불가.');
      }
    }
  };

  async function postRequest(api, data = {}) {
    console.log(data);
    const response = await fetch(`${url}${api}`, {
      method: 'POST',
      mode: 'cors',
      cache: 'no-cache',
      headers: {
        'Content-Type': 'application/json',
        'merApikey': MER_API_KEY,
      },
      redirect: 'follow',
      referrerPolicy: 'no-referrer',
      body: JSON.stringify(data)
    });
    return response;
  }

  app.initANPR();
  return app;
}