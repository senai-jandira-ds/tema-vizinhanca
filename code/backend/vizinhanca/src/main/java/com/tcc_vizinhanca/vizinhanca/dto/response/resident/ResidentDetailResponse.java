package com.tcc_vizinhanca.vizinhanca.dto.response.resident;

import com.tcc_vizinhanca.vizinhanca.dto.response.condominium.CondominiumSummaryResponse;
import com.tcc_vizinhanca.vizinhanca.dto.response.publication.PublicationSummaryResponse;
import com.tcc_vizinhanca.vizinhanca.entity.publication.Publication;
import com.tcc_vizinhanca.vizinhanca.entity.resident.Resident;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class ResidentDetailResponse {
    private Long id;
    private String photo;
    private String name;
    private String apto;
    private String block;
    private String cpf;
    private String email;
    private String phone;
    private Integer score;
    private String creationDate;
    private List<PublicationSummaryResponse> publications;
    private CondominiumSummaryResponse condominium;

    public ResidentDetailResponse(Resident resident) {
        this.id = resident.getId();
        this.photo = resident.getPhoto();
        this.name = resident.getName();
        this.apto = resident.getApartment();
        this.block = resident.getBlock();
        this.cpf = resident.getCpf();
        this.email = resident.getEmail();
        this.phone = resident.getPhone();
        this.score = resident.getScore();
        this.creationDate = resident.getCreationDate() != null
                ? resident.getCreationDate().toString()
                : null;
        this.publications = resident.getPublications() != null
                ? resident.getPublications().stream()
                    .map(PublicationSummaryResponse::new)
                    .toList()
                : List.of();
        this.condominium = resident.getCondominium() != null
                ? new CondominiumSummaryResponse(resident.getCondominium())
                : null;
    }
}
