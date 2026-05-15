/***************************************************
 * Objetivo: DTO de resposta responsável por encapsular
 * os detalhes completos de uma denúncia, utilizado
 * nas operações de busca por ID, criação e atualização
 * Data: 15/05/2026
 * Autor: Leonardo Scotti
 * Versão: 1.0.05.26
 * *************************************************/

package com.tcc_vizinhanca.vizinhanca.dto.response.report;

import com.tcc_vizinhanca.vizinhanca.dto.response.resident.ResidentSummaryResponse;
import com.tcc_vizinhanca.vizinhanca.entity.report.Report;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class ReportDetailResponse {

    private Long id;
    private String description;
    private ResidentSummaryResponse resident;
    private ReasonReportResponse reasonReport;
    private Long objectId;
    private Long serviceId;
    private Long publicationId;

    public ReportDetailResponse(Report report) {
        this.id = report.getId();
        this.description = report.getDescription();
        this.resident = report.getResident() != null
                ? new ResidentSummaryResponse(report.getResident())
                : null;
        this.reasonReport = report.getReasonReport() != null
                ? new ReasonReportResponse(report.getReasonReport())
                : null;
        this.objectId = report.getObject() != null
                ? report.getObject().getId()
                : null;
        this.serviceId = report.getService() != null
                ? report.getService().getId()
                : null;
        this.publicationId = report.getPublication() != null
                ? report.getPublication().getId()
                : null;
    }
}