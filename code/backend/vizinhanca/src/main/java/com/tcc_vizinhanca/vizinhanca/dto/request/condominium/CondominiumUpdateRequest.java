package com.tcc_vizinhanca.vizinhanca.dto.request.condominium;

import com.fasterxml.jackson.annotation.JsonProperty;
import jakarta.validation.constraints.*;
import lombok.Getter;
import lombok.Setter;
import org.springframework.web.multipart.MultipartFile;

@Getter
@Setter
public class CondominiumUpdateRequest {

    @JsonProperty("name")
    @NotBlank(message = "Nome é obrigatório")
    private String nome;

    @NotBlank(message = "CNPJ é obrigatório")
    @Size(min = 14, max = 14, message = "CNPJ deve ter 14 dígitos")
    private String cnpj;

    @JsonProperty("photo")
    @NotBlank(message = "Foto é obrigatório")
    private MultipartFile foto;

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

}


