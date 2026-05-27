package com.tcc_vizinhanca.vizinhanca.dto.request.resident;

import com.fasterxml.jackson.annotation.JsonProperty;
import jakarta.validation.constraints.*;
import lombok.Getter;
import lombok.Setter;
import org.springframework.web.multipart.MultipartFile;

@Getter
@Setter
public class ResidentUpdateRequest {

    private MultipartFile photo;

    @NotBlank(message = "Nome é obrigatório")
    private String name;

    @NotBlank(message = "Apartamento é obrigatório")
    private String apartment;

    @JsonProperty("id_block")
    @NotNull(message = "ID do Bloco deve ser obrigatório")
    @Positive(message = "ID do Bloco deve ser positivo")
    private Long idBlock;

    @NotBlank(message = "CPF é obrigatório")
    @Size(min = 11, max = 11, message = "CPF deve ter 11 dígitos")
    private String cpf;

    @NotBlank(message = "Email é obrigatório")
    @Email(message = "Email inválido")
    private String email;

    @NotBlank(message = "Telefone é obrigatório")
    private String phone;

    @NotNull(message = "Pontuação é obrigatório")
    private Integer score;

    private Boolean is_active;
}
