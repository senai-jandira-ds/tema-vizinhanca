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
