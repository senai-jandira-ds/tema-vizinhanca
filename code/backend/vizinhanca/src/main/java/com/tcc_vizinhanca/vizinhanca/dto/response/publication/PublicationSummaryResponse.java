/***************************************************
 * Objetivo: DTO de resposta resumido responsável por
 * representar as publicações de um morador dentro
 * do detalhamento do ResidentDetailResponse
 * Data: 24/04/2026
 * Autor: Leonardo Scotti
 * Versão: 1.0.04.26
 * *************************************************/

package com.tcc_vizinhanca.vizinhanca.dto.response.publication;

import com.tcc_vizinhanca.vizinhanca.entity.publication.Publication;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class PublicationSummaryResponse {

    private Long id;
    private String photo;
    private String title;
    private String description;
    private String creation_date;

    public PublicationSummaryResponse(Publication publication) {
        this.id = publication.getId();
        this.photo = publication.getPhoto();
        this.title = publication.getTitle();
        this.description = publication.getDescription();
        this.creation_date = publication.getCreationDate() != null
                ? publication.getCreationDate().toString()
                : "";
    }

}
