package com.example.merchant.domain.deploytest;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

// simple api controller for testing deployment
@RestController
@RequestMapping("/api/v1/deploy-test")
public class DeloyTest {

    @GetMapping("/hello")
    public ResponseEntity<String> hello() {
        return ResponseEntity.ok("Hello, World!");
    }
}
