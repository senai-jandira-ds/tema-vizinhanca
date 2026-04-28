package com.tcc_vizinhanca.vizinhanca.dto.request;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class CondominiumRequest {
    private String nome;
    private String cnpj;
    private Integer qtdBlocos, qtdApto;
    private String email;
    private String senha;
}
