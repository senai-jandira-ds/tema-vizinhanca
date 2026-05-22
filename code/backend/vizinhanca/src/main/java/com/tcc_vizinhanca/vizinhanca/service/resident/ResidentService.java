/***************************************************
 * Objetivo: Serviço responsável pelas regras de negócio relacionadas
 * à entidade Resident, incluindo operações de CRUD, validações
 * e regras de gerenciamento de moradores
 * Data: 24/04/2026
 * Autor: Leonardo Scotti
 * Versão: 1.0.04.26
 * *************************************************/

package com.tcc_vizinhanca.vizinhanca.service.resident;

import com.tcc_vizinhanca.vizinhanca.dto.request.resident.ResidentUpdateRequest;
import com.tcc_vizinhanca.vizinhanca.entity.condominium.Block;
import com.tcc_vizinhanca.vizinhanca.entity.condominium.Condominium;
import com.tcc_vizinhanca.vizinhanca.entity.resident.Resident;
import com.tcc_vizinhanca.vizinhanca.mapper.resident.ResidentMapper;
import com.tcc_vizinhanca.vizinhanca.repository.resident.ResidentRepository;
import com.tcc_vizinhanca.vizinhanca.service.block.BlockService;
import com.tcc_vizinhanca.vizinhanca.service.condominium.CondominiumService;
import com.tcc_vizinhanca.vizinhanca.service.email.EmailService;
import com.tcc_vizinhanca.vizinhanca.service.storage.BlobStorageService;
import com.tcc_vizinhanca.vizinhanca.service.util.PasswordGeneratorUtils;
import lombok.NonNull;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.server.ResponseStatusException;

import java.util.List;

@Service
public class ResidentService {

    @Autowired
    private ResidentRepository residentRepository;

    @Autowired
    private CondominiumService condominiumService;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @Autowired
    private EmailService emailService;

    @Autowired
    private BlockService blockService;

    @Autowired
    private BlobStorageService blobStorageService;

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
    public List<Resident> getSelectResidentsByCondominiumId(Long idCondominium) {

        Condominium condominium = condominiumService.getSelectCondominiumById(idCondominium);

        return residentRepository
                .findByCondominiumId(condominium.getId());
    }

    // SELECT BY EMAIL
    public Resident getSelectResidentByEmail(String email) {
        return residentRepository.findByEmail(email)
                .orElseThrow(() -> new ResponseStatusException(
                        HttpStatus.NOT_FOUND, "Morador não encontrado!"));
    }

    // INSERT RESIDENT
    public Resident setInsertResident(@NonNull Resident resident) {
        resident.setId(null);

        if (residentRepository.existsByCpf(resident.getCpf())) {
            throw new ResponseStatusException(HttpStatus.CONFLICT, "CPF já cadastrado!");
        }

//        if (residentRepository.existsByEmail(resident.getEmail())) {
//            throw new ResponseStatusException(HttpStatus.CONFLICT, "E-mail já cadastrado!");
//        }

        String rawPassword = PasswordGeneratorUtils.generateSecure(8);

        resident.setPassword(passwordEncoder.encode(rawPassword));
        resident.setIsActive(true);

        Boolean emailSent = emailService.sendWelcomeEmail(
                resident.getEmail(),
                resident.getName(),
                rawPassword
        );

        if (!emailSent) {
            throw new ResponseStatusException(
                    HttpStatus.INTERNAL_SERVER_ERROR,
                    "Erro ao enviar email. Erro na SERVICE."
            );
        }

        return residentRepository.save(resident);
    }

    // UPDATE RESIDENT
    public Resident setUpdateResident(
            @NonNull ResidentUpdateRequest dto,
            MultipartFile photo,
            Long idResident) {

        Resident existingResident = getSelectResidentById(idResident);

        if(photo != null && !photo.isEmpty()){

            if (existingResident.getPhoto() != null && !existingResident.getPhoto().isEmpty()){
                blobStorageService.deleteFile(existingResident.getPhoto());
            }

            String newPhoto = blobStorageService
                    .uploadFile(photo, "residents");

            existingResident.setPhoto(newPhoto);
        }

        if (dto.getCpf() != null && !dto.getCpf().equals(existingResident.getCpf())) {
            if (residentRepository.existsByCpf(dto.getCpf())) {
                throw new ResponseStatusException(HttpStatus.CONFLICT, "CPF já cadastrado!");
            }
        }

//        if (dto.getEmail() != null && !dto.getEmail().equals(existingResident.getEmail())) {
//            if (residentRepository.existsByEmail(dto.getEmail())) {
//                throw new ResponseStatusException(HttpStatus.CONFLICT, "E-mail já cadastrado!");
//            }
//        }

        Block block = dto.getIdBlock() != null
                ? blockService.getSelectBlockById(dto.getIdBlock())
                : null;

        ResidentMapper.updateEntity(dto, existingResident, block);

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
