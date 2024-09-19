package com.example.mobipay.oauth2.dto;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class UserDTO {

    //    private String username;
    private String email;
    private String name;
    private String picture;
    private String phonenumber;
    private String role;

}
