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
    public void afterConnectionEstablished(WebSocketSession session) throws WebSocketException {
        log.info("Connected: " + session.getId());
        CLIENTS.put(session.getId(), session);
        try{
            final String payload = objectMapper.writeValueAsString(new SessionId(session.getId()));
            session.sendMessage(new TextMessage(payload));
        }catch (Exception e){
            throw new WebSocketException(e.getMessage());
        }
    }

    @Override
    public void afterConnectionClosed(WebSocketSession session, CloseStatus status) {
        log.info("Disconnected: " + session.getId());
        CLIENTS.remove(session.getId());
    }

    @Override
    protected void handleTextMessage(WebSocketSession session, TextMessage message) throws WebSocketException {
        try{
            MerchantInfo info = objectMapper.readValue(message.getPayload(), MerchantInfo.class);
            MERCHANT_SESSIONID.put(info.getType(), session.getId());
            log.info("Received message: " + message.getPayload() + " | MERCHANT_SESSION: " + MERCHANT_SESSIONID);
        }catch (Exception e){
            throw new WebSocketException(e.getMessage());
        }
    }

    /**
     * Send the payment result to the client
     * @param type
     * @param result
     * @throws Exception
     */
    public void sendResult(MerchantType type, PaymentResult result) throws WebSocketException {
        try{
            final String payload = objectMapper.writeValueAsString(result);
            CLIENTS.get(MERCHANT_SESSIONID.get(type)).sendMessage(new TextMessage(payload));
            log.info("Sent message: " + payload + " -> " + MERCHANT_SESSIONID.get(type));
        }catch (Exception e){
            throw new WebSocketException(e.getMessage());
        }
    }
}
