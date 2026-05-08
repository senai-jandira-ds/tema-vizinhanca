package com.tcc_vizinhanca.vizinhanca.mapper;

import com.tcc_vizinhanca.vizinhanca.dto.request.condominium.CondominiumCreateRequest;
import com.tcc_vizinhanca.vizinhanca.dto.request.condominium.CondominiumUpdateRequest;
import com.tcc_vizinhanca.vizinhanca.entity.condominium.Condominium;
import com.tcc_vizinhanca.vizinhanca.entity.condominium.CondominiumAddress;

public class CondominiumMapper {

    public static Condominium toEntity(CondominiumCreateRequest dto) {
        Condominium condominium = Condominium.builder()
                .name(dto.getNome())
                .cnpj(dto.getCnpj())
                .amountBlocks(dto.getQtdBlocos())
                .amountApartments(dto.getQtdApto())
                .email(dto.getEmail())
                .password(dto.getPassword())
                .build();

        if (dto.getAddress() != null) {
            CondominiumAddress  condominiumAddress = CondominiumAddress.builder()
                    .cep(dto.getAddress().getCep())
                    .street(dto.getAddress().getStreet())
                    .neighborhood(dto.getAddress().getNeighborhood())
                    .number(dto.getAddress().getNumber())
                    .landmark(dto.getAddress().getLandmark())
                    .city(dto.getAddress().getCity())
                    .state(dto.getAddress().getState())
                    .condominium(condominium)
                    .build();
        }

        return condominium;
    }

    public static Condominium updateEntity(CondominiumUpdateRequest dto, Condominium entity) {

        if (dto.getNome() != null) {
            entity.setName(dto.getNome());
        }

        if (dto.getCnpj() != null) {
            entity.setCnpj(dto.getCnpj());
        }

        if (dto.getQtdBlocos() != null) {
            entity.setAmountBlocks(dto.getQtdBlocos());
        }

        if (dto.getQtdApto() != null) {
            entity.setAmountApartments(dto.getQtdApto());
        }

        if (dto.getEmail() != null) {
            entity.setEmail(dto.getEmail());
        }

        return entity;

    }

}
