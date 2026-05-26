package com.tcc_vizinhanca.vizinhanca.dto.response.resident;

import com.tcc_vizinhanca.vizinhanca.dto.response.block.BlockSummaryResponse;
import com.tcc_vizinhanca.vizinhanca.dto.response.condominium.CondominiumSummaryResponse;
import com.tcc_vizinhanca.vizinhanca.dto.response.publication.PublicationSummaryResponse;
import com.tcc_vizinhanca.vizinhanca.dto.response.service.ServiceSummaryResponse;
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
    private String apartment;
    private String cpf;
    private String email;
    private String phone;
    private Integer score;
    private String creationDate;
    private BlockSummaryResponse block;
    private List<PublicationSummaryResponse> publications;
    private List<ServiceSummaryResponse> services;
    private CondominiumSummaryResponse condominium;

    public ResidentDetailResponse(Resident resident) {
        this.id = resident.getId();
        this.photo = resident.getPhoto();
        this.name = resident.getName();
        this.apartment = resident.getApartment();
        this.cpf = resident.getCpf();
        this.email = resident.getEmail();
        this.phone = resident.getPhone();
        this.score = resident.getScore();
        this.block = resident.getBlock() != null
                ? new BlockSummaryResponse(resident.getBlock())
                : new BlockSummaryResponse();
        this.creationDate = resident.getCreationDate() != null
                ? resident.getCreationDate().toString()
                : "";
        this.publications = resident.getPublications() != null
                ? resident.getPublications().stream()
                    .map(PublicationSummaryResponse::new)
                    .toList()
                : List.of();
        this.services = resident.getServices() != null
                ? resident.getServices().stream()
                    .map(ServiceSummaryResponse::new)
                    .toList()
                : List.of();
        this.condominium = resident.getCondominium() != null
                ? new CondominiumSummaryResponse(resident.getCondominium())
                : new CondominiumSummaryResponse();
    }
}
