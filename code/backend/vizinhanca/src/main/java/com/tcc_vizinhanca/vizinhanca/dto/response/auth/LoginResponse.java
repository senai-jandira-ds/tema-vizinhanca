package com.tcc_vizinhanca.vizinhanca.dto.response.auth;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class LoginResponse<T> {

    private String token;

    public LoginResponse(String token) {
        this.token = token;
    }

    private T user;
}
