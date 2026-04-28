package com.tcc_vizinhanca.vizinhanca.dto.response;

import com.tcc_vizinhanca.vizinhanca.entity.condominium.Condominium;
import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class CondominiumDetailResponse {
    private Long id;
    private String nome;
    private String cnpj;
    private Integer qtdBlocos, qtdApto;
    private String email;

    public CondominiumDetailResponse(Condominium condominium) {
        this.id = condominium.getId();
        this.nome = condominium.getName();
        this.cnpj = condominium.getCnpj();
        this.qtdBlocos = condominium.getAmountBlocks();
        this.qtdApto = condominium.getAmountApartments();
        this.email = condominium.getEmail();
    }
}
