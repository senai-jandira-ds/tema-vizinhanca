package com.tcc_vizinhanca.vizinhanca.controller.auth;

import com.tcc_vizinhanca.vizinhanca.dto.auth.LoginRequest;
import com.tcc_vizinhanca.vizinhanca.dto.response.ApiResponse;
import com.tcc_vizinhanca.vizinhanca.dto.response.AuthResponse;
import com.tcc_vizinhanca.vizinhanca.dto.response.CondominiumDetailResponse;
import com.tcc_vizinhanca.vizinhanca.dto.response.ResidentDetailResponse;
import com.tcc_vizinhanca.vizinhanca.service.auth.AuthService;
import com.tcc_vizinhanca.vizinhanca.util.ResponseUtil;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/v1/auth")
public class AuthController {

    private final AuthService authService;

    public AuthController(AuthService authService) {
        this.authService = authService;
    }

    @PostMapping("/login/condominium")
    public ResponseEntity<ApiResponse<AuthResponse<CondominiumDetailResponse>>> loginCondominium(@RequestBody LoginRequest request) {

        AuthResponse<CondominiumDetailResponse> response = authService.loginCondominium(request.getEmail(), request.getPassword());

        return ResponseEntity.ok(ResponseUtil.success(response, "Usuário logado com sucesso!"));

    }

    @PostMapping("/login/resident")
    public ResponseEntity<ApiResponse<AuthResponse<ResidentDetailResponse>>> loginResident(@RequestBody LoginRequest request) {

        AuthResponse<ResidentDetailResponse> response = authService.loginResident(request.getEmail(), request.getPassword());

        return ResponseEntity.ok(ResponseUtil.success(response, "Usuário logado com sucesso!"));
    }
}