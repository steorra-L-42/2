let sendBtn = document.getElementById('send');
sendBtn.addEventListener('click', function() {
    const socket = new WebSocket('ws://localhost:8080/api/v1/merchants/websocket'); 
    // const socket = new WebSocket('wss://merchant.mobipay.kr/api/v1/merchants/websocket'); 

    let sessionId; // 세션 ID를 저장할 변수

    socket.onopen = function(event) {
        console.log('WebSocket is open now.');
    };

    socket.onmessage = function(event) {
        const message = JSON.parse(event.data); // 메시지를 JSON 객체로 파싱
        if (message.sessionId) {
            sessionId = message.sessionId; // 세션 ID 저장
            console.log('Session ID:', sessionId);
            socket.send(JSON.stringify({ // 가맹점 정보를 서버로 전송
                "type" : sendBtn.getAttribute('merchant_type') // 가맹점 타입 전송
            }));
        } else {
            console.log('Received message:', message);
            // 수신한 메시지에 따른 행동을 수행
            // JSON 형식 참고
            // {
            // 	"success": true, // 결제 성공 실패
            // 	"type": "PARKING", // 가맹점 종류
            // 	"paymentBalance": 5000, // 결제 금액
            // 	"info": "입차시간 1:34, 출차시간 5:45"// 결제 정보
            // }
            document.getElementById('message').value += JSON.stringify(message) + '\n';
            // 실제로는 success가 true일 때만 socket.close()를 호출해야 함
            socket.close(); // 세션 종료
        }
    };

    socket.onclose = function(event) {
        console.log('WebSocket is closed now.');
    };

    socket.onerror = function(error) {
        console.log('WebSocket error:', error);
    };
});