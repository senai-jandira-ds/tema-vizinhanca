package com.tcc_vizinhanca.vizinhanca.controller.auth;

import com.tcc_vizinhanca.vizinhanca.dto.auth.LoginRequest;
import com.tcc_vizinhanca.vizinhanca.dto.response.ApiResponse;
import com.tcc_vizinhanca.vizinhanca.dto.response.auth.AuthResponse;
import com.tcc_vizinhanca.vizinhanca.dto.response.condominium.CondominiumDetailResponse;
import com.tcc_vizinhanca.vizinhanca.dto.response.resident.ResidentDetailResponse;
import com.tcc_vizinhanca.vizinhanca.entity.condominium.ActivityView;
import com.tcc_vizinhanca.vizinhanca.entity.condominium.Condominium;
import com.tcc_vizinhanca.vizinhanca.entity.resident.Resident;
import com.tcc_vizinhanca.vizinhanca.security.jwt.JwtService;
import com.tcc_vizinhanca.vizinhanca.service.auth.AuthService;
import com.tcc_vizinhanca.vizinhanca.service.condominium.ActivityViewService;
import com.tcc_vizinhanca.vizinhanca.service.condominium.CondominiumService;
import com.tcc_vizinhanca.vizinhanca.service.resident.ResidentService;
import com.tcc_vizinhanca.vizinhanca.util.ResponseUtil;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/v1/auth")
@Tag(name = "Login", description = "Endpoints para login.")
public class AuthController {

    private final AuthService authService;

    @Autowired
    private JwtService jwtService;

    @Autowired
    private CondominiumService condominiumService;

    @Autowired
    private ResidentService residentService;

    @Autowired
    private ActivityViewService activityViewService;

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

    // GET ME - CONDOMINIUM
    @GetMapping("/me/condominium")
    public ResponseEntity<ApiResponse<CondominiumDetailResponse>> meCondominium(
            HttpServletRequest request) {

        String token = request.getHeader("Authorization").substring(7);
        String email = jwtService.extrairUsername(token);

        Condominium condominium = condominiumService.getSelectCondominiumByEmail(email);
        List<ActivityView> activities = activityViewService
                .getSelectActivitiesViewByCondominiumId(condominium.getId());

        CondominiumDetailResponse response = new CondominiumDetailResponse(condominium, activities);

        return ResponseEntity.ok(ResponseUtil.success(response, "Condomínio encontrado com sucesso!"));
    }

    // GET ME - RESIDENT
    @GetMapping("/me/resident")
    public ResponseEntity<ApiResponse<ResidentDetailResponse>> meResident(
            HttpServletRequest request) {

        String token = request.getHeader("Authorization").substring(7);
        String email = jwtService.extrairUsername(token);

        Resident resident = residentService.getSelectResidentByEmail(email);

        ResidentDetailResponse response = new ResidentDetailResponse(resident);

        return ResponseEntity.ok(ResponseUtil.success(response, "Morador encontrado com sucesso!"));
    }
}