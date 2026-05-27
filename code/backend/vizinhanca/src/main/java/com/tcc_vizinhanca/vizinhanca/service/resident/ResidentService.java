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
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.server.ResponseStatusException;

import java.util.List;
import java.util.concurrent.CompletableFuture;

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
    public Page<Resident> getSelectAllResidents(Pageable pageable) {
        return residentRepository.findAll(pageable);
    }

    // SELECT BY ID
    public Resident getSelectResidentById(@NonNull Long id) {
        return residentRepository.findById(id)
                .orElseThrow(() -> new ResponseStatusException(
                        HttpStatus.NOT_FOUND, "Morador não encontrado no Banco de Dados!"));
    }

    // SELECT BY CONDOMINIUM ID
    public Page<Resident> getSelectResidentsByCondominiumId(Long idCondominium, Pageable pageable) {
        Condominium condominium = condominiumService.getSelectCondominiumById(idCondominium);
        return residentRepository.findByCondominiumId(condominium.getId(), pageable);
    }

    // SELECT BY BLOCK
    public Page<Resident> getSelectResidentsByCondominiumIdAndBlockId(Long condominiumId, Long blockId, Pageable pageable) {
        return residentRepository.findByCondominiumIdAndBlockId(condominiumId, blockId, pageable);
    }

    // SELECT BY STATUS
    public Page<Resident> getSelectResidentsByCondominiumIdAndIsActive(Long condominiumId, Boolean isActive, Pageable pageable) {
        return residentRepository.findByCondominiumIdAndIsActive(condominiumId, isActive, pageable);
    }

    // SELECT BY EMAIL
    public Resident getSelectResidentByEmail(String email) {
        return residentRepository.findByEmail(email)
                .orElseThrow(() -> new ResponseStatusException(
                        HttpStatus.NOT_FOUND, "Morador não encontrado!"));
    }

    @Transactional(readOnly = true)
    public Resident getDetailedResidentByEmail(String email) {

        Resident base = residentRepository
                .findWithBasicDetailsByEmail(email)
                .orElseThrow(() -> new ResponseStatusException(
                        HttpStatus.NOT_FOUND,
                        "Morador não encontrado."
                ));

        residentRepository.findWithPublicationsByEmail(email);
        residentRepository.findWithServicesByEmail(email);

        return base;
    }

    // INSERT RESIDENT
    public Resident setInsertResident(@NonNull Resident resident) {
        resident.setId(null);

        if (residentRepository.existsByCpf(resident.getCpf())) {
            throw new ResponseStatusException(HttpStatus.CONFLICT, "CPF já cadastrado!");
        }

        String rawPassword = PasswordGeneratorUtils.generateSecure(8);

        resident.setPassword(passwordEncoder.encode(rawPassword));
        resident.setIsActive(true);

        emailService.sendWelcomeEmail(resident.getEmail(), resident.getName(), rawPassword);

        return residentRepository.save(resident);
    }

    // UPDATE RESIDENT
    public Resident setUpdateResident(
            @NonNull ResidentUpdateRequest dto,
            MultipartFile photo,
            Long idResident) {

        Resident existingResident = getSelectResidentById(idResident);

        if (photo != null && !photo.isEmpty()) {
            if (existingResident.getPhoto() != null) {
                blobStorageService.deleteFile(existingResident.getPhoto());
            }
            String newPhoto = blobStorageService.uploadFile(photo, "residents");
            existingResident.setPhoto(newPhoto);
        }

        if (dto.getCpf() != null && !dto.getCpf().equals(existingResident.getCpf())) {
            if (residentRepository.existsByCpf(dto.getCpf())) {
                throw new ResponseStatusException(HttpStatus.CONFLICT, "CPF já cadastrado!");
            }
        }

        Block block = dto.getIdBlock() != null
                ? blockService.getSelectBlockById(dto.getIdBlock())
                : null;

        ResidentMapper.updateEntity(dto, existingResident, block);

        return residentRepository.save(existingResident);
    }

    // DELETE RESIDENT
    public void setDeleteResidentById(Long idResident) {
        if (!residentRepository.existsById(idResident)) {
            throw new ResponseStatusException(
                    HttpStatus.NOT_FOUND, "Morador não encontrado no Banco de Dados!");
        }
        residentRepository.deleteById(idResident);
    }
}
