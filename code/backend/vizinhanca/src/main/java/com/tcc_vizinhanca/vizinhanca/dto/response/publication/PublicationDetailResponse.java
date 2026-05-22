/***************************************************
 * Objetivo: DTO de resposta responsável por encapsular
 * os detalhes completos de uma publicação, utilizado
 * nas operações de busca por ID, criação e atualização
 * Data: 24/04/2026
 * Autor: Leonardo Scotti
 * Versão: 1.0.04.26
 * *************************************************/

package com.tcc_vizinhanca.vizinhanca.dto.response.publication;

import com.tcc_vizinhanca.vizinhanca.entity.publication.Publication;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDateTime;

@Getter
@Setter
@NoArgsConstructor
public class PublicationDetailResponse {

    private Long id;
    private String photo;
    private String title;
    private String description;
    private LocalDateTime creationDate;
    private Long residentId;
    private String residentName;

    public PublicationDetailResponse(Publication publication) {
        this.id = publication.getId();
        this.photo = publication.getPhoto();
        this.title = publication.getTitle();
        this.description = publication.getDescription();
        this.creationDate = publication.getCreationDate();
        this.residentId = publication.getResident().getId();
        this.residentName = publication.getResident().getName();
    }
}
