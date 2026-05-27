/***************************************************
 * Objetivo: Serviço responsável pela autenticação de usuários,
 * realizando validação de credenciais, integração com repositórios
 * de Condominium e Resident e geração de token JWT para acesso à API
 * Data: 24/04/2026
 * Autor: Leonardo Scotti
 * Versão: 1.0.04.26
 * *************************************************/

package com.tcc_vizinhanca.vizinhanca.service.auth;

import com.tcc_vizinhanca.vizinhanca.dto.response.auth.AuthResponse;
import com.tcc_vizinhanca.vizinhanca.dto.response.condominium.ActivityViewDetailResponse;
import com.tcc_vizinhanca.vizinhanca.dto.response.condominium.CondominiumDetailResponse;
import com.tcc_vizinhanca.vizinhanca.dto.response.condominium.LoginCondominiumResponse;
import com.tcc_vizinhanca.vizinhanca.dto.response.resident.ResidentDetailResponse;
import com.tcc_vizinhanca.vizinhanca.entity.condominium.ActivityView;
import com.tcc_vizinhanca.vizinhanca.entity.condominium.Condominium;
import com.tcc_vizinhanca.vizinhanca.entity.resident.Resident;
import com.tcc_vizinhanca.vizinhanca.repository.condominium.CondominiumRepository;
import com.tcc_vizinhanca.vizinhanca.repository.resident.ResidentRepository;
import com.tcc_vizinhanca.vizinhanca.security.jwt.JwtService;
import com.tcc_vizinhanca.vizinhanca.service.condominium.ActivityViewService;
import com.tcc_vizinhanca.vizinhanca.service.resident.ResidentService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import java.util.List;

@Service
public class AuthService {

    @Autowired
    private CondominiumRepository condominiumRepository;

    @Autowired
    private ResidentRepository residentRepository;

    @Autowired
    private ResidentService residentService;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @Autowired
    private JwtService jwtService;

    @Autowired
    private ActivityViewService activityViewService;

    public AuthResponse<LoginCondominiumResponse> loginCondominium(String email, String password){

        Condominium condominium = condominiumRepository.findByEmail(email)
                .orElseThrow(() -> new ResponseStatusException(
                        HttpStatus.UNAUTHORIZED, "Credenciais inválidas!"
                ));

        if (!passwordEncoder.matches(password, condominium.getPassword())) {
            throw new ResponseStatusException(
                    HttpStatus.UNAUTHORIZED, "Credenciais inválidas!"
            );
        }

        String token = jwtService.gerarToken(
                condominium.getEmail(),
                condominium.getId(),
                null,
                "CONDOMÍNIO");


        List<ActivityViewDetailResponse> activities =
                activityViewService
                        .getSelectActivitiesViewByCondominiumId(
                                condominium.getId()
                        )
                        .stream()
                        .map(ActivityViewDetailResponse::new)
                        .toList();

        LoginCondominiumResponse loginCondominiumResponse = new LoginCondominiumResponse(condominium, activities);

        return new AuthResponse<>(token, loginCondominiumResponse);
    }

    public AuthResponse<ResidentDetailResponse> loginResident(String email, String password) {

        Resident resident = residentRepository.findByEmail(email)
                .orElseThrow(() -> new ResponseStatusException(
                        HttpStatus.UNAUTHORIZED, "Credenciais inválidas!"));

        if (!passwordEncoder.matches(password, resident.getPassword())) {
            throw new ResponseStatusException(HttpStatus.UNAUTHORIZED, "Credenciais inválidas!");
        }

        Resident detailedResident = residentService.getDetailedResidentByEmail(email);

        String token = jwtService.gerarToken(
                detailedResident.getEmail(),
                detailedResident.getCondominium().getId(),
                detailedResident.getId(),
                "MORADOR");

        ResidentDetailResponse userResponse = new ResidentDetailResponse(detailedResident);

        return new AuthResponse<>(token, userResponse);
    }
}
