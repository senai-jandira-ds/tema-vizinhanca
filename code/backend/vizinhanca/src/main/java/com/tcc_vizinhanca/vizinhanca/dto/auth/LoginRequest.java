package com.tcc_vizinhanca.vizinhanca.dto.auth;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class LoginRequest {

    public String login;
    public String password;
}
