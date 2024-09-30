package com.example.merchant.global.enums;

public enum MerchantType {

    PARKING("주차장"),
    OIL("주유소"),
    FOOD("식음료"),
    WASHING("세차장"),
    MOTEL("무인텔"),
    STREET("노상 주차장");

    private final String name;

    MerchantType(String name) {
        this.name = name;
    }

    public String getName() {
        return name;
    }

}
