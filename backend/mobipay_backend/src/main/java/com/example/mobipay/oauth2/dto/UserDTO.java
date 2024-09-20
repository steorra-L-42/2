package com.example.mobipay.oauth2.dto;

import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class UserDTO {

    //    private String username;
    private Long userId;
    private String email;
    private String name;
    private String picture;
    private String phonenumber;
    private String role;

}
