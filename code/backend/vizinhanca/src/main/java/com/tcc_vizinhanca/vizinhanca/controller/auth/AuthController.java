package com.tcc_vizinhanca.vizinhanca.controller.auth;

import com.tcc_vizinhanca.vizinhanca.dto.auth.LoginRequest;
import com.tcc_vizinhanca.vizinhanca.security.jwt.JwtService;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/v1/auth")
public class AuthController {

    private final JwtService jwtService;

    public AuthController(JwtService jwtService) {
        this.jwtService = jwtService;
    }

    @PostMapping("/login")
    public String login(@RequestBody LoginRequest loginRequest) {
        // Aqui você valida no banco
        if (!"admin".equals(loginRequest.login) || !"123".equals(loginRequest.password)) {
            throw new RuntimeException("Credenciais inválidas");
        }

        return jwtService.gerarToken(loginRequest.login);
    }
}
