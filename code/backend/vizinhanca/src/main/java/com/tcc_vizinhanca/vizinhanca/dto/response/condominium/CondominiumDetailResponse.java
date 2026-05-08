package com.tcc_vizinhanca.vizinhanca.dto.response.condominium;

import com.tcc_vizinhanca.vizinhanca.dto.response.condominium_address.CondominiumAddressResponse;
import com.tcc_vizinhanca.vizinhanca.dto.response.resident.ResidentDetailResponse;
import com.tcc_vizinhanca.vizinhanca.dto.response.resident.ResidentSummaryResponse;
import com.tcc_vizinhanca.vizinhanca.entity.condominium.Condominium;
import lombok.*;

import java.util.ArrayList;
import java.util.List;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class CondominiumDetailResponse {
    private Long id;
    private String name;
    private String cnpj;
    private Integer amount_blocks, amount_apartments;
    private String email;
    private String creation_date;
    private List<ResidentSummaryResponse> residents;
    private CondominiumAddressResponse address;

    public CondominiumDetailResponse(Condominium condominium) {
        this.id = condominium.getId();
        this.name = condominium.getName();
        this.cnpj = condominium.getCnpj();
        this.amount_blocks = condominium.getAmountBlocks();
        this.amount_apartments = condominium.getAmountApartments();
        this.email = condominium.getEmail();
        this.creation_date = condominium.getCreationDate() != null
                ? condominium.getCreationDate().toString()
                : null;
        this.residents = condominium.getResidents() != null
                ? condominium.getResidents().stream()
                .map(ResidentSummaryResponse::new)
                .toList()
                : null;
        this.address = condominium.getAddress() != null
                ? new CondominiumAddressResponse(condominium.getAddress())
                : null;
    }
}
