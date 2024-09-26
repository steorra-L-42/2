package com.example.mobipay.oauth2.error;

public class MissingUserDetailsException extends RuntimeException {
    private String email;
    private String picture;

    public MissingUserDetailsException(String message) {
        super(message);
        this.email = email;
        this.picture = picture;
    }

    public String getEmail() {
        return email;
    }

    public String getPicture() {
        return picture;
    }
}
