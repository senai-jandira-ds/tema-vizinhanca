/***************************************************
 * Objetivo: DTO de resposta responsável por encapsular
 * os dados do motivo de denúncia retornado nas
 * requisições de consulta
 * Data: 15/05/2026
 * Autor: Leonardo Scotti
 * Versão: 1.0.05.26
 * *************************************************/

package com.tcc_vizinhanca.vizinhanca.dto.response.report;

import com.tcc_vizinhanca.vizinhanca.entity.report.ReasonReport;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class ReasonReportResponse {

    private Long id;
    private String name;

    public ReasonReportResponse(ReasonReport reasonReport) {
        this.id = reasonReport.getId();
        this.name = reasonReport.getName();
    }
}