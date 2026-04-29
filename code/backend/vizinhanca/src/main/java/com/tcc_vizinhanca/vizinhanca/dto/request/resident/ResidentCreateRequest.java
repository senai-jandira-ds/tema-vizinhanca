package com.tcc_vizinhanca.vizinhanca.dto.request.resident;

import com.fasterxml.jackson.annotation.JsonProperty;
import jakarta.validation.constraints.*;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class ResidentCreateRequest {

    @JsonProperty("photo")
    private String foto;

    @JsonProperty("name")
    @NotBlank(message = "Nome é obrigatório")
    private String nome;

    @JsonProperty("apartment")
    @NotBlank(message = "Apartamento é obrigatório")
    private String apto;

    @JsonProperty("block")
    @NotBlank(message = "Bloco é obrigatório")
    private String bloco;

    @NotBlank(message = "CPF é obrigatório")
    @Size(min = 11, max = 11, message = "CPF deve ter 11 dígitos")
    private String cpf;

    @NotBlank(message = "Email é obrigatório")
    @Email(message = "Email inválido")
    private String email;

    @JsonProperty("phone")
    @NotBlank(message = "Telefone é obrigatório")
    private String telefone;

    @JsonProperty("score")
    @NotNull(message = "Pontuação é obrigatório")
    private Integer pontuacao;

    @JsonProperty("condominium_id")
    @NotNull(message = "ID do Condomínio deve ser obrigatório")
    @Positive(message = "ID do Condomínio deve ser positivo")
    private Long idCondominium;
}
