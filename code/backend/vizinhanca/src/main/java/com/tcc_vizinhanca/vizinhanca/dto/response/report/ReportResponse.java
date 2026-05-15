/***************************************************
 * Objetivo: DTO de resposta responsável por encapsular
 * a listagem de denúncias retornada nas requisições
 * de consulta geral
 * Data: 15/05/2026
 * Autor: Leonardo Scotti
 * Versão: 1.0.05.26
 * *************************************************/

package com.tcc_vizinhanca.vizinhanca.dto.response.report;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonPropertyOrder;
import com.tcc_vizinhanca.vizinhanca.entity.report.Report;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
@NoArgsConstructor
@JsonPropertyOrder({"amount_reports", "reports"})
public class ReportResponse {

    @JsonProperty("amount_reports")
    public Integer getAmountReports() {
        return reports != null ? reports.size() : 0;
    }

    private List<ReportDetailResponse> reports;

    public ReportResponse(List<Report> reports) {
        this.reports = reports.stream()
                .map(ReportDetailResponse::new)
                .toList();
    }
}