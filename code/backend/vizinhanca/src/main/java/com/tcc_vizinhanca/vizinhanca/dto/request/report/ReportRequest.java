/***************************************************
 * Objetivo: DTO de requisição responsável por transportar
 * os dados necessários para criação e atualização
 * de uma denúncia na aplicação
 * Data: 15/05/2026
 * Autor: Leonardo Scotti
 * Versão: 1.0.05.26
 * *************************************************/

package com.tcc_vizinhanca.vizinhanca.dto.request.report;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class ReportRequest {

    @NotBlank(message = "Descrição é obrigatória")
    private String description;

    @NotNull(message = "Motivo da denúncia é obrigatório")
    private Long reasonReportId;

    private Long objectId;
    private Long serviceId;
    private Long publicationId;
}