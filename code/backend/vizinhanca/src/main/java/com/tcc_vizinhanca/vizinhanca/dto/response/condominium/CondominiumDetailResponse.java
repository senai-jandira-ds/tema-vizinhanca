package com.tcc_vizinhanca.vizinhanca.dto.response.condominium;

import com.tcc_vizinhanca.vizinhanca.dto.response.condominium_address.CondominiumAddressResponse;
import com.tcc_vizinhanca.vizinhanca.dto.response.resident.ResidentSummaryResponse;
import com.tcc_vizinhanca.vizinhanca.dto.response.service.ServiceDetailResponse;
import com.tcc_vizinhanca.vizinhanca.entity.condominium.ActivityView;
import com.tcc_vizinhanca.vizinhanca.entity.condominium.Condominium;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.ArrayList;
import java.util.List;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class CondominiumDetailResponse {
    private Long id;
    private String name;
    private String photo;
    private String cnpj;
    private Integer amount_blocks, amount_apartments;
    private String email;
    private String creation_date;
    private List<ResidentSummaryResponse> residents;
    private List<ServiceDetailResponse> services;
    private CondominiumAddressResponse address;
    private List<ActivityViewDetailResponse> activities;

    public CondominiumDetailResponse(Condominium condominium) {
        this.id = condominium.getId();
        this.name = condominium.getName();
        this.photo = condominium.getPhoto();
        this.cnpj = condominium.getCnpj();
        this.amount_blocks = condominium.getAmountBlocks();
        this.amount_apartments = condominium.getAmountApartments();
        this.email = condominium.getEmail();
        this.creation_date = condominium.getCreationDate() != null
                ? condominium.getCreationDate().toString()
                : "";
        this.residents = condominium.getResidents() != null
                ? condominium.getResidents().stream()
                .map(ResidentSummaryResponse::new)
                .toList()
                : new ArrayList<>();
        this.services = condominium.getServices() != null
            ? condominium.getServices().stream()
              .map(ServiceDetailResponse::new)
              .toList()
            : new ArrayList<>();
        this.address = condominium.getAddress() != null
                ? new CondominiumAddressResponse(condominium.getAddress())
                : new CondominiumAddressResponse();
    }

    public CondominiumDetailResponse(Condominium condominium, List<ActivityView> activities) {
        this.id = condominium.getId();
        this.name = condominium.getName();
        this.photo = condominium.getPhoto();
        this.cnpj = condominium.getCnpj();
        this.amount_blocks = condominium.getAmountBlocks();
        this.amount_apartments = condominium.getAmountApartments();
        this.email = condominium.getEmail();
        this.creation_date = condominium.getCreationDate() != null
                ? condominium.getCreationDate().toString()
                : "";
        this.residents = condominium.getResidents() != null
                ? condominium.getResidents().stream()
                  .map(ResidentSummaryResponse::new)
                  .toList()
                : new ArrayList<>();
        this.address = condominium.getAddress() != null
                ? new CondominiumAddressResponse(condominium.getAddress())
                : new CondominiumAddressResponse();
        this.activities = activities != null
                ? activities.stream()
                  .map(ActivityViewDetailResponse::new)
                  .toList()
                : new ArrayList<>();
    }
}
