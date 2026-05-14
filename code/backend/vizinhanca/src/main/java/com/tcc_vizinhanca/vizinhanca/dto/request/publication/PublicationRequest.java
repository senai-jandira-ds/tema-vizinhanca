package com.tcc_vizinhanca.vizinhanca.dto.request.publication;

import jakarta.validation.constraints.NotBlank;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class PublicationRequest {

    @NotBlank(message = "Foto é obrigatória")
    private String photo;

    @NotBlank(message = "Título é obrigatório")
    private String title;

    @NotBlank(message = "Descrição é obrigatória")
    private String description;

}
