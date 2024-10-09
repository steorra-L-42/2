package com.example.merchant.domain.menu.controller;

import com.example.merchant.domain.menu.dto.MenuListRequest;
import com.example.merchant.domain.menu.service.MenuService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

@RequiredArgsConstructor
@RestController
public class MenuController {

    private final MenuService menuService;

    @PostMapping("/api/v1/merchants/{merchantType}/menu-list")
    public ResponseEntity<?> getMenuList(@RequestHeader("merApiKey") String merApiKey,
                                         @PathVariable("merchantType") String merchantType,
                                         @RequestBody @Valid MenuListRequest menuListRequest) {

        return menuService.getMenuList(merApiKey, merchantType, menuListRequest);
    }

}
