/***************************************************
 * Objetivo: DTO responsável por receber os dados de atualização de um serviço
 * Data: 15/05/2026
 * Autor: Leonardo Scotti
 * Versão: 1.0.05.26
 * *************************************************/

package com.tcc_vizinhanca.vizinhanca.dto.request.service;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.tcc_vizinhanca.vizinhanca.enums.Status;
import jakarta.validation.constraints.Positive;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class ServiceUpdateRequest {

    private String photo;
    private String title;

    @JsonProperty("estimated_time")
    private Integer estimatedTime;

    private String urgency;
    private String description;
    private Status status;

    @JsonProperty("category_id")
    @Positive(message = "ID da categoria deve ser positivo")
    private Long categoryId;
}