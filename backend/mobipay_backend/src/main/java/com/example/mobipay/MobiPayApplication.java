package com.example.mobipay;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.data.jpa.repository.config.EnableJpaAuditing;

@EnableJpaAuditing
@SpringBootApplication
@EnableJpaAuditing
public class MobiPayApplication {

    public static void main(String[] args) {
        SpringApplication.run(MobiPayApplication.class, args);
    }

}
