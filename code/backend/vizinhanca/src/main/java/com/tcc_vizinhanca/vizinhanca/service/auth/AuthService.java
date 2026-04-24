/***************************************************
 * Objetivo: Serviço responsável pela autenticação de usuários,
 * realizando validação de credenciais, integração com repositórios
 * de Condominium e Resident e geração de token JWT para acesso à API
 * Data: 24/04/2026
 * Autor: Leonardo Scotti
 * Versão: 1.0.04.26
 * *************************************************/

package com.tcc_vizinhanca.vizinhanca.service.auth;

import com.tcc_vizinhanca.vizinhanca.entity.condominium.Condominium;
import com.tcc_vizinhanca.vizinhanca.entity.resident.Resident;
import com.tcc_vizinhanca.vizinhanca.repository.condominium.CondominiumRepository;
import com.tcc_vizinhanca.vizinhanca.repository.resident.ResidentRepository;
import com.tcc_vizinhanca.vizinhanca.security.jwt.JwtService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

@Service
public class AuthService {

    @Autowired
    private CondominiumRepository condominiumRepository;

    @Autowired
    private ResidentRepository residentRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @Autowired
    private JwtService jwtService;

    public String login(String email, String password){

        Condominium condominium = condominiumRepository.findByEmail(email).orElse(null);

        if (condominium != null) {
            if (!passwordEncoder.matches(password, condominium.getPassword())) {
                throw new ResponseStatusException(HttpStatus.UNAUTHORIZED, "Credenciais inválidas");
            }

            return jwtService.gerarToken(condominium.getEmail());
        }

        Resident resident = residentRepository.findByEmail(email).orElse(null);

        if (resident != null) {
            if (!passwordEncoder.matches(password, resident.getPassword())) {
                throw new ResponseStatusException(HttpStatus.UNAUTHORIZED, "Credenciais inválidas");
            }

            return jwtService.gerarToken(resident.getEmail());
        }

        throw new ResponseStatusException(HttpStatus.UNAUTHORIZED, "Usuário não encontrado");

    }
}
