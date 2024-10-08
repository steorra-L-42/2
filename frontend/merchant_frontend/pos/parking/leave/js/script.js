let lpnumber = null;
const url = "https://merchant.mobipay.kr/api/v1";
const MERCHANT_TYPE = 'PARKING';
const MERCHANT_TYPE_URL = 'parking';
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
    socket: null,
    isManualLPnoModalOpen: false,
    isShowCameraChooseModal: false,
    lastLpno: '',
    lpnoCounter: 0,
    detectionStopped: false,
    manualLpno: '',
    cameraDevices: [],
    prettyEntryDate: "",
    prettyEntryTime: "",
    prettyExitDate: "",
    prettyExitTime: "",
    paymentBalance: 0,
    entryTime: "",

    initVideo() {
      this.video = document.getElementById('video');
      this.detectCameras();
    },

    openCameraChooseModal() {
      this.isShowCameraChooseModal = true;
    },

    closeCameraChooseModal() {
      this.isShowCameraChooseModal = false;
    },

    openLPnoModal() {
      this.isManualLPnoModalOpen = true;
    },

    closeLPnoModal() {
      this.isManualLPnoModalOpen = false;
      this.manualLpno = '';
    },

    async detectCameras() {
      try {
        const devices = await navigator.mediaDevices.enumerateDevices();
        this.cameraDevices = devices.filter(device => device.kind === 'videoinput');
      } catch (error) {
        console.error('카메라 뭐뭐 있는지 파악 실패:', error);
      }
    },

    async selectCamera(deviceId) {
      try {
        const stream = await navigator.mediaDevices.getUserMedia({
          video: {
            deviceId: { exact: deviceId },
            width: { ideal: 1920 },
            height: { ideal: 1080 },
            frameRate: { ideal: 60 }
          }
        });

        this.video.srcObject = stream;
        this.video.play();
        this.closeCameraChooseModal();
      } catch (error) {
        console.error('Failed to select camera:', error);
      }
    },

    submitManualLpno() {
      if (this.manualLpno.trim() !== '') {
        this.lpno = this.manualLpno;
        this.isMobiUser = true;
      }
      this.closeLPnoModal();
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

    // getTotalPrice() {
    //   return this.cart.reduce(
    //       (total, item) => total + item.qty * item.price,
    //       0
    //   );
    // },

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

    closeModalSuccess() {
      this.isShowModalSuccess = false;
    },

    resumeDetection() {
      this.detectionStopped = false;
      this.lpno = '';
      this.lastLpno = '';
      this.lpnoCounter= 0;
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


    cancelLoading() {
      if (this.socket) {
        this.socket.close();
        this.socket = null;
      }
      this.isLoading = false;
    },

    requestPayMobi(json) {
      console.log("requestPayMobi 함수 안에 들어왔음.");
      console.log(this);
      this.closeModalReceipt();
      this.isLoading = true;

      // websocket 연결
      this.socket = new WebSocket('wss://merchant.mobipay.kr/api/v1/merchants/websocket');

      let sessionId; // 세션 ID를 저장할 변수

      this.socket.onopen = async (event) => {
        console.log('WebSocket is open now.');

        // prettyEntrytime을 원하는 형식으로 설정
        const entryDate = new Date(json.entry);
        const exitDate = new Date(json.exit);

        // 두 시간의 차이를 계산
        const timeDifference = exitDate - entryDate; // milliseconds
        const hours = Math.floor(timeDifference / (1000 * 60 * 60)); // 시간을 구함
        const minutes = Math.floor((timeDifference % (1000 * 60 * 60)) / (1000 * 60)); // 분을 구함

        let paymentBalance = json.paymentBalance;
        let info = `${hours}시간 ${minutes}분 : ${paymentBalance}원`;
        let carNumber = json.carNumber || "번호 인식 실패";

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

        console.log("결제 요청 전 body를 찍어보겠습니다. : " + JSON.stringify(paymentRequest));

        try {
          const response = await postRequest('/merchants/payments/request', paymentRequest);
          if(!response.ok){
            // 결제 실패시 다시 detect를 합니다.
            console.log("결제를 실패했으므로 detect를 다시 시작하겠습니다.");
            this.resumeDetection();
            throw new Error(`postRequest() : error! status: ${response.status}`);
          } else {
            // 결제 성공했으니 화면 구성을 해야함.
            console.log('결제 요청 성공, 결제 결과 대기 중...');
          }
        } catch (error) {
          console.error('결제 요청 실패:', error);
          // 웹소켓 연결 해제
          //socket.close();
          this.socket.close();
          alert('결제 요청 실패');
        }
      };

      this.socket.onclose = (event) => {
        console.log('WebSocket is closed now.');
      };

      this.socket.onerror = (error) => {
        console.log('WebSocket error:', error);
      };

      this.socket.onmessage = (event) => {
        const message = JSON.parse(event.data);

        if (message.sessionId) {
          sessionId = message.sessionId;
          this.socket.send(JSON.stringify({ "type": MERCHANT_TYPE }));
        } else {
          if (message.success) {

            this.isLoading = false;
            this.isShowModalSuccess = true;
            this.lpno = null;
            this.isMobiUser = false;
            this.cart = [];
            this.prettyExitDate = "";
            this.prettyExitTime = "";
            this.prettyEntryDate = "";
            this.prettyEntryTime = "";
            console.log(this.entryTime);
            this.entryTime = "";
            console.log(this.entryTime);
            this.paymentBalance = 0;
            this.socket.close();
            alert('결제 성공');
            this.resumeDetection();
          } else {
            this.isLoading = false;

            this.prettyExitDate = null;
            this.prettyExitTime = null;
            this.prettyEntryDate = null;
            this.prettyEntryTime = null;
            this.entryTime = null;
            this.paymentBalance = 0;

            alert('결제 거절');
          }
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
      if (!this.detectionStopped){
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
                        const detectedLpno = data.predicted_text;

                        if (self.lastLpno !== detectedLpno) {
                          self.lpnoCounter = 1;
                          self.lastLpno = detectedLpno;
                        } else {
                          self.lpnoCounter++;
                        }

                        if (self.lpnoCounter >= 3) {
                          self.detectionStopped = true;
                          self.$data.lpno = detectedLpno;
                          app.lpno = detectedLpno;
                          self.$data.isMobiUser = true;
                          console.log("차량번호 초기화됐나..? : " + self.$data.lpno);


                          try {
                            // 첫 번째 요청
                            const response1 = await fetch(url + '/merchants/parking/exit', {
                              method: 'PATCH',
                              headers: {
                                'Content-Type': 'application/json',
                                'merApiKey': 'Da19J03F6g7H8iB2c54e'
                              },
                              body: JSON.stringify({
                                "carNumber": self.$data.lpno,
                                "exit": new Date(new Date().getTime() + (9 * 60 * 60 * 1000)).toISOString()
                              }),
                            });
                            console.log('response1 status:', response1.status);

                            const jsonResponse1 = await response1.json(); // 응답을 JSON 형태로 파싱

                            const entryDate = new Date(jsonResponse1.entry);
                            if (isNaN(entryDate.getTime())) {
                              alert("입차 하지 않은 차량 입니다.");
                              this.resumeDetection();
                            } else {
                              self.$data.entryTime = entryDate;

                              self.$data.prettyEntryDate = `${entryDate.getFullYear()}-${(entryDate.getMonth() + 1).toString().padStart(2, '0')}-${entryDate.getDate().toString().padStart(2, '0')}`;
                              self.$data.prettyEntryTime = `${entryDate.getHours()}시 ${entryDate.getMinutes()}분 ${entryDate.getSeconds()}초`;

                              const exitDate = new Date(jsonResponse1.exit);
                              self.$data.prettyExitDate = `${exitDate.getFullYear()}-${(exitDate.getMonth() + 1).toString().padStart(2, '0')}-${exitDate.getDate().toString().padStart(2, '0')}`;
                              self.$data.prettyExitTime = `${exitDate.getHours()}시 ${exitDate.getMinutes()}분 ${exitDate.getSeconds()}초`;

                              self.$data.paymentBalance = jsonResponse1.paymentBalance;

                              console.log("출차 시간 : " + self.$data.prettyEntryTime);
                              console.log("출차 시 요금 : " + self.$data.paymentBalance);

                              console.log("출차 완료되었음. 결제 요청 보냅니다.");
                              // 두 번째 요청
                              const paymentRequestData = {
                                "entry": jsonResponse1.entry,
                                "exit": jsonResponse1.exit,
                                "paymentBalance": jsonResponse1.paymentBalance,
                                "carNumber": self.$data.lpno
                                // 필요한 추가 데이터가 있다면 여기에 추가
                              };

                              console.log(paymentRequestData);

                              this.requestPayMobi(paymentRequestData);
                              console.log("requestPayMobi 끝");
                              console.log("detection을 resume하는 함수 호출완료");
                            }

                          } catch (error) {
                            console.log('Fetch error:', error); // 에러 발생 시 콘솔에 출력
                          }

                        } else {
                          self.$data.lpno = detectedLpno;
                          self.$data.isMobiUser = true;
                        }
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
      }else{
        // Detection 중지 상태면 2초 간격으로 함수 실행, 실제 detection 수행 안 함 (로컬 GPU 사용 X)
        setTimeout(() => {
          // this.resumeDetection();
          console.log("detection을 다시 시작하겠습니다. : " + this.detectionStopped);
          requestAnimationFrame(() => this.detectObjects());
        }, 2000);
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
    },

    // 결제 취소
    isShowTransactionListModal: false,
    transactionList: [],

    openTransactionListModal() {
        this.isShowTransactionListModal = true;
    },

    closeTransactionListModal() {
        this.isShowTransactionListModal = false;
    },

    async getTransactions() {
        const url = `/api/v1/merchants/${MERCHANT_TYPE_URL}/transactions`;
        const data = await getRequest(url);
        this.transactionList = data?.items;
    },

    cancelTransaction(transactionUniqueNo) {
        const url = `/api/v1/merchants/${MERCHANT_TYPE_URL}/cancelled-transactions/${transactionUniqueNo}`;
        patchRequest(url).then(() => {
            this.getTransactions();
        });
    },
    // 결제 취소
  };

  async function postRequest(api, data = {}) {
    console.log("postRequest함수 실행");
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

    // if(!response.ok){
    //   throw new Error(`postRequest() : error! status: ${response.status}`);
    // }

    return response;
  }

  async function getRequest(url) {
      try {
          const response = await fetch(url, {
              method: 'GET',
              headers: {
                  'merApiKey': MER_API_KEY,
              },
          });

          if(response?.status !== 200) {
              alert('거래내역 조회에 실패했습니다.');
              console.error('Error:', response);
              return;
          }

          const data = await response.json();
          // console.log(data);
          return data

      } catch (error) {
          console.error('Error:', error);
          return;
      }
  }

  async function patchRequest(url) {
      try {
          const response = await fetch(url, {
              method: 'PATCH',
              headers: {
                  'merApiKey': MER_API_KEY,
              },
          });

          if(response?.status !== 200) {
              alert('거래 취소에 실패했습니다.');
              console.error('Error:', response);
              return;
          }

          alert('거래가 취소되었습니다.');

      } catch (error) {
          console.error('Error:', error);
          return;
      }
  }


  window.app = app;

  app.initANPR();
  return app;
}
