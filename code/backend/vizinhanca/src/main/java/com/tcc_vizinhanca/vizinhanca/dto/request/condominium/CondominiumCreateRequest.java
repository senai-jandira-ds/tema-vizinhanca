package com.tcc_vizinhanca.vizinhanca.dto.request.condominium;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.tcc_vizinhanca.vizinhanca.dto.request.condominium_address.CondominiumAddressRequest;
import jakarta.validation.Valid;
import jakarta.validation.constraints.*;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class CondominiumCreateRequest {

    @JsonProperty("name")
    @NotBlank(message = "Nome é obrigatório")
    private String nome;

    @NotBlank(message = "CNPJ é obrigatório")
    @Size(min = 14, max = 14, message = "CNPJ deve ter 14 dígitos")
    private String cnpj;

    @JsonProperty("photo")
    private String foto;

    @JsonProperty("amount_blocks")
    @NotNull(message = "Quantidade de blocos é obrigatória")
    @Positive(message = "Quantidade de blocos deve ser positiva")
    private Integer qtdBlocos;

    @JsonProperty("amount_apartments")
    @NotNull(message = "Quantidade de apartamentos é obrigatória")
    @Positive(message = "Quantidade de apartamentos deve ser positiva")
    private Integer qtdApto;

    @NotBlank(message = "E-mail é obrigatório")
    @Email(message = "E-mail inválido")
    private String email;

    @NotBlank(message = "Senha é obrigatória")
    @Size(min = 8, message = "Senha deve ter no mínimo 8 caracteres")
    private String password;

    @JsonProperty("address")
    @NotNull(message = "Endereço é obrigatório")
    @Valid
    private CondominiumAddressRequest address;
}
