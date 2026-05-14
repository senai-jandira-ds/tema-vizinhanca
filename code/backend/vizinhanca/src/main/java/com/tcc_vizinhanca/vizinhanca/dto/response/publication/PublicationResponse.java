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
