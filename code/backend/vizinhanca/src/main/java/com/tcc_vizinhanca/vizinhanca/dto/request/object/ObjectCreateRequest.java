package com.tcc_vizinhanca.vizinhanca.dto.request.object;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.tcc_vizinhanca.vizinhanca.enums.StatusObject;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.Setter;
import org.springframework.web.multipart.MultipartFile;

import java.time.LocalDate;

@Getter
@Setter
public class ObjectCreateRequest {

    @NotNull(message = "Foto é obrigatória")
    private MultipartFile photo;

    @NotBlank(message = "Título é obrigatório")
    private String title;

    @NotNull(message = "Prazo de disponibilidade é obrigatório")
    private String  deadline;

    @NotBlank(message = "Descrição é obrigatória")
    private String description;

    @NotBlank(message = "Status é obrigatório")
    private String status;

    @JsonProperty("category_id")
    @NotNull(message = "Categoria é obrigatória")
    private Long categoryId;
}