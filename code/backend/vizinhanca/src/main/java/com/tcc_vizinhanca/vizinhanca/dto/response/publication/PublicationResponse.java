/***************************************************
 * Objetivo: DTO de resposta responsável por encapsular
 * a listagem de publicações retornada nas requisições
 * de consulta geral
 * Data: 24/04/2026
 * Autor: Leonardo Scotti
 * Versão: 1.0.04.26
 * *************************************************/

package com.tcc_vizinhanca.vizinhanca.dto.response.publication;

import com.tcc_vizinhanca.vizinhanca.entity.publication.Publication;
import lombok.Getter;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
public class PublicationResponse {

    private Integer amountPublications;
    private List<PublicationDetailResponse> publications;

    public PublicationResponse(List<Publication> publications) {
        this.amountPublications = publications.size();
        this.publications = publications.stream()
                .map(PublicationDetailResponse::new)
                .toList();
    }
}
