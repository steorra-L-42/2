package com.example.merchant.domain.payment.service;

import com.example.merchant.domain.payment.dto.PaymentResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@RequiredArgsConstructor
@Service
public class PaymentService {

    public PaymentResponse request() {

        // send a request to the mobipay server

        return null;
    }

    public void result() {

        // send a result to client
        // close the socket connection

        return;
    }
}
