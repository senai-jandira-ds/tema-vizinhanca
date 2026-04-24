package com.tcc_vizinhanca.vizinhanca.controller.auth;

import com.tcc_vizinhanca.vizinhanca.dto.auth.LoginRequest;
import com.tcc_vizinhanca.vizinhanca.security.jwt.JwtService;
import com.tcc_vizinhanca.vizinhanca.service.AuthService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/v1/auth")
public class AuthController {

    @Autowired
    private AuthService authService;

    @PostMapping("/login")
    public ResponseEntity<?> login(@RequestBody LoginRequest loginRequest){

        String token = authService.login(
                loginRequest.getLogin(),
                loginRequest.getPassword()
        );

        return ResponseEntity.ok(token);
    }
}
