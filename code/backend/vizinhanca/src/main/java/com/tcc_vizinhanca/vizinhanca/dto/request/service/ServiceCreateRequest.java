/***************************************************
 * Objetivo: DTO responsável por receber os dados de criação de um serviço
 * Data: 15/05/2026
 * Autor: Leonardo Scotti
 * Versão: 1.0.05.26
 * *************************************************/

package com.tcc_vizinhanca.vizinhanca.dto.request.service;

import com.fasterxml.jackson.annotation.JsonProperty;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class ServiceCreateRequest {

    @NotBlank(message = "Foto é obrigatória")
    private String photo;

    @NotBlank(message = "Título é obrigatório")
    private String title;

    @JsonProperty("estimated_time")
    @NotNull(message = "Tempo estimado é obrigatório")
    @Positive(message = "Tempo estimado deve ser positivo")
    private Integer estimatedTime;

    @NotBlank(message = "Urgência é obrigatória")
    private String urgency;

    @NotBlank(message = "Descrição é obrigatória")
    private String description;

    @NotBlank(message = "Status é obrigatório")
    private String status;

    @JsonProperty("category_id")
    @NotNull(message = "Categoria é obrigatória")
    @Positive(message = "ID da categoria deve ser positivo")
    private Long categoryId;
}