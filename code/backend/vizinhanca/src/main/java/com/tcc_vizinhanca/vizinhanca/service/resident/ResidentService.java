/***************************************************
 * Objetivo: Serviço responsável pelas regras de negócio relacionadas
 * à entidade Resident, incluindo operações de CRUD, validações
 * e regras de gerenciamento de moradores
 * Data: 24/04/2026
 * Autor: Leonardo Scotti
 * Versão: 1.0.04.26
 * *************************************************/

package com.tcc_vizinhanca.vizinhanca.service.resident;

import com.tcc_vizinhanca.vizinhanca.dto.request.resident.ResidentCreateRequest;
import com.tcc_vizinhanca.vizinhanca.dto.request.resident.ResidentUpdateRequest;
import com.tcc_vizinhanca.vizinhanca.dto.response.resident.ResidentResponse;
import com.tcc_vizinhanca.vizinhanca.entity.condominium.Condominium;
import com.tcc_vizinhanca.vizinhanca.entity.resident.Resident;
import com.tcc_vizinhanca.vizinhanca.mapper.ResidentMapper;
import com.tcc_vizinhanca.vizinhanca.repository.condominium.CondominiumRepository;
import com.tcc_vizinhanca.vizinhanca.repository.resident.ResidentRepository;
import com.tcc_vizinhanca.vizinhanca.service.email.EmailService;
import com.tcc_vizinhanca.vizinhanca.service.util.PasswordGeneratorUtils;
import jakarta.mail.MessagingException;
import lombok.NonNull;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import java.util.List;

@Service
public class ResidentService {

    @Autowired
    private ResidentRepository residentRepository;

    @Autowired
    private CondominiumRepository condominiumRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @Autowired
    private EmailService emailService;

    // SELECT ALL
    public List<Resident> getSelectAllResidents(){
        return residentRepository.findAll();
    }

    // SELECT BY ID
    public Resident getSelectResidentById(@NonNull Long id){
        Resident resident = residentRepository.findById(id)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Morador não encontrado no Banco de Dados!"));

        return resident;
    }

    // SELECT BY CONDOMINIUM ID
    public List<Resident> getSelectResidentsByCondominiumEmail(String email) {

        Condominium condominium = condominiumRepository
                .findByEmail(email)
                .orElseThrow(() ->
                        new RuntimeException("Condomínio não encontrado"));

        return residentRepository
                .findByCondominiumId(condominium.getId());
    }

    // INSERT RESIDENT
    public Resident setInsertResident(@NonNull Resident resident) {
        resident.setId(null);

        if (residentRepository.existsByCpf(resident.getCpf())) {
            throw new ResponseStatusException(HttpStatus.CONFLICT, "CPF já cadastrado!");
        }

        if (residentRepository.existsByEmail(resident.getEmail())) {
            throw new ResponseStatusException(HttpStatus.CONFLICT, "E-mail já cadastrado!");
        }

        String rawPassword = PasswordGeneratorUtils.generateSecure(8);

        resident.setPassword(passwordEncoder.encode(rawPassword));
        resident.setIsActive(true);

        Resident saved = residentRepository.save(resident);

        try {
            emailService.sendWelcomeEmail(
                    saved.getEmail(),
                    saved.getName(),
                    rawPassword
            );
        } catch (MessagingException e) {
            System.err.println("Erro ao enviar e-mail: " + e.getMessage());
        }

        return saved;
    }

    // UPDATE RESIDENT
    public Resident setUpdateResident(@NonNull ResidentUpdateRequest dto, Long idResident) {
        Resident existingResident = getSelectResidentById(idResident);

        if (dto.getCpf() != null && !dto.getCpf().equals(existingResident.getCpf())) {
            if (residentRepository.existsByCpf(dto.getCpf())) {
                throw new ResponseStatusException(HttpStatus.CONFLICT, "CPF já cadastrado!");
            }
        }

        if (dto.getEmail() != null && !dto.getEmail().equals(existingResident.getEmail())) {
            if (residentRepository.existsByEmail(dto.getEmail())) {
                throw new ResponseStatusException(HttpStatus.CONFLICT, "E-mail já cadastrado!");
            }
        }

        ResidentMapper.updateEntity(dto, existingResident);

        return residentRepository.save(existingResident);
    }

    // DELETE RESIDENT
    public void setDeleteResidentById(Long idResident){
        if (!residentRepository.existsById(idResident)) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Morador não  encontrado no Banco de Dados!");
        }

        residentRepository.deleteById(idResident);
    }
}
