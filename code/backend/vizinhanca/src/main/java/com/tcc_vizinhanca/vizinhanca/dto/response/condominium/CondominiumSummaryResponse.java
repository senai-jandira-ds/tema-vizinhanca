package com.tcc_vizinhanca.vizinhanca.dto.response.condominium;

import com.tcc_vizinhanca.vizinhanca.entity.condominium.Condominium;
import jdk.jfr.Name;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDate;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class CondominiumSummaryResponse {
    private Long id;
    private String name;
    private String photo;
    private String cnpj;
    private Integer amount_blocks, amount_apartments;
    private String email;
    private String creation_date;

    public CondominiumSummaryResponse(Condominium condominium) {
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
    }

}
