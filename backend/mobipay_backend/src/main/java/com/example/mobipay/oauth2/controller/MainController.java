package com.example.mobipay.oauth2.controller;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;

//@Controller
@RestController
public class MainController {

    @GetMapping("/")
    @ResponseBody
    public String mainAPI() {
        return "main";
    }
}
