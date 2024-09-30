package com.example.merchant.util.pos;

import com.example.merchant.domain.payment.dto.PaymentResult;
import com.example.merchant.global.enums.MerchantType;
import com.example.merchant.util.pos.dto.MerchantInfo;
import com.example.merchant.util.pos.dto.SessionId;
import com.example.merchant.util.pos.error.WebSocketException;
import com.fasterxml.jackson.databind.ObjectMapper;
import java.util.concurrent.ConcurrentHashMap;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.socket.CloseStatus;
import org.springframework.web.socket.TextMessage;
import org.springframework.web.socket.WebSocketSession;
import org.springframework.web.socket.handler.TextWebSocketHandler;

@RequiredArgsConstructor
@Component
public class WebSocketHandler extends TextWebSocketHandler {

    private static final Logger log = LoggerFactory.getLogger(WebSocketHandler.class);
    private static final ConcurrentHashMap<String, WebSocketSession> CLIENTS = new ConcurrentHashMap<>();
    private static final ConcurrentHashMap<MerchantType, String> MERCHANT_SESSIONID = new ConcurrentHashMap<>();

    private final ObjectMapper objectMapper;

    @Override
    public void afterConnectionEstablished(WebSocketSession session) throws Exception {
        log.info("Connected: " + session.getId());
        CLIENTS.put(session.getId(), session);
        final String payload = objectMapper.writeValueAsString(new SessionId(session.getId()));
        session.sendMessage(new TextMessage(payload));
    }

    @Override
    public void afterConnectionClosed(WebSocketSession session, CloseStatus status) {
        log.info("Disconnected: " + session.getId());
        CLIENTS.remove(session.getId());
    }

    @Override
    protected void handleTextMessage(WebSocketSession session, TextMessage message) throws Exception {
        MerchantInfo info = objectMapper.readValue(message.getPayload(), MerchantInfo.class);
        MERCHANT_SESSIONID.put(info.getType(), session.getId());
        log.info("Received message: " + message.getPayload() + " | MERCHANT_SESSION: " + MERCHANT_SESSIONID);
        // TODO: sendResult 지우기
        // 3초 후 결제 결과 전송
        Thread.sleep(3000);
        sendResult(info.getType(), new PaymentResult(true, info.getType(), 10000, "test 응답 - 결제 기능 구현 중"));
    }

    /**
     * Send the payment result to the client
     * @param type
     * @param result
     * @throws Exception
     */
    public void sendResult(MerchantType type, PaymentResult result) throws Exception {
        final String payload = objectMapper.writeValueAsString(result);
        CLIENTS.get(MERCHANT_SESSIONID.get(type)).sendMessage(new TextMessage(payload));
    }

    @ExceptionHandler(Exception.class)
    public void handleException(Exception e) {
        log.error("Error", e);
        throw new WebSocketException(e.getMessage());
    }
}
