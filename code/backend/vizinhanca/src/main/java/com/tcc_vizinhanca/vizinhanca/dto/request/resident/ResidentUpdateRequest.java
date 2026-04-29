package com.tcc_vizinhanca.vizinhanca.dto.request.resident;

import com.fasterxml.jackson.annotation.JsonProperty;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.Size;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class ResidentUpdateRequest {

    @JsonProperty("photo")
    private String foto;

    @JsonProperty("name")
    private String nome;

    @JsonProperty("apartment")
    private String apto;

    @JsonProperty("block")
    private String bloco;

    @Size(min = 11, max = 11, message = "CPF deve ter 11 dígitos")
    private String cpf;

    @Email(message = "Email inválido")
    private String email;

    @JsonProperty("phone")
    private String telefone;

    @JsonProperty("score")
    private Integer pontuacao;

    @JsonProperty("condominium_id")
    private Long condominiumId;
}
