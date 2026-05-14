package com.tcc_vizinhanca.vizinhanca.mapper.resident;

import com.tcc_vizinhanca.vizinhanca.dto.request.resident.ResidentCreateRequest;
import com.tcc_vizinhanca.vizinhanca.dto.request.resident.ResidentUpdateRequest;
import com.tcc_vizinhanca.vizinhanca.entity.condominium.Condominium;
import com.tcc_vizinhanca.vizinhanca.entity.resident.Resident;

public class ResidentMapper {

    public static Resident toEntity(ResidentCreateRequest dto, Condominium condominium) {
        return Resident.builder()
                .photo(dto.getFoto())
                .name(dto.getNome())
                .apartment(dto.getApto())
                .block(dto.getBloco())
                .cpf(dto.getCpf())
                .email(dto.getEmail())
                .phone(dto.getTelefone())
                .score(dto.getPontuacao())
                .condominium(condominium)
                .build();
    }

    public static Resident updateEntity(ResidentUpdateRequest dto, Resident entity) {

        if (dto.getFoto() != null) {
            entity.setPhoto(dto.getFoto());
        }

        if (dto.getNome() != null) {
            entity.setName(dto.getNome());
        }

        if (dto.getApto() != null) {
            entity.setApartment(dto.getApto());
        }

        if (dto.getBloco() != null) {
            entity.setBlock(dto.getBloco());
        }

        if (dto.getCpf() != null) {
            entity.setCpf(dto.getCpf());
        }

        if (dto.getEmail() != null) {
            entity.setEmail(dto.getEmail());
        }

        if (dto.getPontuacao() != null) {
            entity.setScore(dto.getPontuacao());
        }

        return entity;
    }

}
