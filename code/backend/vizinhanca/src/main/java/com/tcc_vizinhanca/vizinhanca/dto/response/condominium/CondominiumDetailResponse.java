package com.tcc_vizinhanca.vizinhanca.dto.response.condominium;

import com.tcc_vizinhanca.vizinhanca.entity.condominium.Condominium;
import lombok.*;

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
    }
}
