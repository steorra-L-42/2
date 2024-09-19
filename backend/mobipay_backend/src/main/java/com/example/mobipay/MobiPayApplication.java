package com.example.mobipay;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.data.jpa.repository.config.EnableJpaAuditing;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;

@EnableJpaAuditing
@SpringBootApplication
@EnableJpaRepositories(basePackages = "com.example.mobipay.oauth2.repository")
public class MobiPayApplication {

    public static void main(String[] args) {
        SpringApplication.run(MobiPayApplication.class, args);
    }

}
