package com.tcc_vizinhanca.vizinhanca.mapper.resident;

import com.tcc_vizinhanca.vizinhanca.dto.request.resident.ResidentCreateRequest;
import com.tcc_vizinhanca.vizinhanca.dto.request.resident.ResidentUpdateRequest;
import com.tcc_vizinhanca.vizinhanca.entity.condominium.Block;
import com.tcc_vizinhanca.vizinhanca.entity.condominium.Condominium;
import com.tcc_vizinhanca.vizinhanca.entity.resident.Resident;

public class ResidentMapper {

    public static Resident toEntity(ResidentCreateRequest dto, Condominium condominium, Block block) {
        return Resident.builder()
                .photo(dto.getPhoto())
                .name(dto.getName())
                .apartment(dto.getApartment())
                .block(block)
                .cpf(dto.getCpf())
                .email(dto.getEmail())
                .phone(dto.getPhone())
                .score(dto.getScore())
                .condominium(condominium)
                .build();
    }

    public static Resident updateEntity(ResidentUpdateRequest dto, Resident entity, Block block) {

        if (dto.getPhone() != null) entity.setPhoto(dto.getPhoto());
        if (dto.getName() != null) entity.setName(dto.getName());
        if (dto.getApartment() != null) entity.setApartment(dto.getApartment());
        if (dto.getCpf() != null) entity.setCpf(dto.getCpf());
        if (dto.getEmail() != null) entity.setEmail(dto.getEmail());
        if (dto.getScore() != null) entity.setScore(dto.getScore());
        if (block != null) entity.setBlock(block);

        return entity;
    }

}
