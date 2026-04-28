package com.tcc_vizinhanca.vizinhanca.mapper;

import com.tcc_vizinhanca.vizinhanca.dto.request.CondominiumRequest;
import com.tcc_vizinhanca.vizinhanca.entity.condominium.Condominium;

public class CondominiumMapper {

    public static Condominium toEntity(CondominiumRequest dto) {
        return Condominium.builder()
                .name(dto.getNome())
                .cnpj(dto.getCnpj())
                .amountBlocks(dto.getQtdBlocos())
                .amountApartments(dto.getQtdApto())
                .email(dto.getEmail())
                .password(dto.getSenha())
                .build();
    }

}
