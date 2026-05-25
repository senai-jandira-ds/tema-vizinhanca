package com.tcc_vizinhanca.vizinhanca.dto.response.condominium;

import com.tcc_vizinhanca.vizinhanca.dto.response.block.BlockSummaryResponse;
import com.tcc_vizinhanca.vizinhanca.dto.response.condominium_address.CondominiumAddressResponse;
import com.tcc_vizinhanca.vizinhanca.dto.response.report.ReportSummaryResponse;
import com.tcc_vizinhanca.vizinhanca.dto.response.resident.ResidentSummaryResponse;
import com.tcc_vizinhanca.vizinhanca.dto.response.service.ServiceDetailResponse;
import com.tcc_vizinhanca.vizinhanca.entity.condominium.Condominium;
import lombok.Getter;
import lombok.Setter;

import java.util.ArrayList;
import java.util.List;

@Getter
@Setter
public class LoginCondominiumResponse {

    private Long id;
    private String name;
    private String photo;
    private String cnpj;
    private Integer amount_blocks, amount_apartments;
    private String email;
    private String creation_date;
    private CondominiumAddressResponse address;
    private List<ActivityViewDetailResponse> activities;

    public LoginCondominiumResponse(Condominium condominium, List<ActivityViewDetailResponse> activities) {
        this.id = condominium.getId();
        this.name = condominium.getName();
        this.photo = condominium.getPhoto() != null
                ? condominium.getPhoto()
                : "";
        this.cnpj = condominium.getCnpj();
        this.amount_blocks = condominium.getAmountBlocks();
        this.amount_apartments = condominium.getAmountApartments();
        this.email = condominium.getEmail();
        this.creation_date = condominium.getCreationDate() != null
                ? condominium.getCreationDate().toString()
                : "";
        this.address = condominium.getAddress() != null
                ? new CondominiumAddressResponse(condominium.getAddress())
                : new CondominiumAddressResponse();
        this.activities = activities != null
                ? activities
                : new ArrayList<>();
    }

}
