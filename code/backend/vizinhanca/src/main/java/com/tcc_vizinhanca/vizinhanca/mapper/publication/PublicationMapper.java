/***************************************************
 * Objetivo: Mapper responsável pela conversão entre
 * os DTOs de requisição e a entidade Publication,
 * utilizado nas operações de criação e atualização
 * Data: 14/05/2026
 * Autor: Leonardo Scotti
 * Versão: 1.0.04.26
 * *************************************************/

package com.tcc_vizinhanca.vizinhanca.mapper.publication;

import com.tcc_vizinhanca.vizinhanca.dto.request.publication.PublicationRequest;
import com.tcc_vizinhanca.vizinhanca.entity.publication.Publication;

public class PublicationMapper {

    public static Publication toEntity(PublicationRequest dto) {
        return Publication.builder()
                .photo(dto.getPhoto())
                .title(dto.getTitle())
                .description(dto.getDescription())
                .build();
    }

    public static Publication updateEntity(PublicationRequest dto, Publication entity) {

        if (dto.getPhoto() != null) {
            entity.setPhoto(dto.getPhoto());
        }

        if (dto.getTitle() != null) {
            entity.setTitle(dto.getTitle());
        }

        if (dto.getDescription() != null) {
            entity.setDescription(dto.getDescription());
        }

        return entity;
    }
}
