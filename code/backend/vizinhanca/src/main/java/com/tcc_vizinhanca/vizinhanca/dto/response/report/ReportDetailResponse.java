/***************************************************
 * Objetivo: DTO de resposta responsável por encapsular
 * os detalhes completos de uma denúncia, utilizado
 * nas operações de busca por ID, criação e atualização
 * Data: 15/05/2026
 * Autor: Leonardo Scotti
 * Versão: 1.0.05.26
 * *************************************************/

package com.tcc_vizinhanca.vizinhanca.dto.response.report;

import com.tcc_vizinhanca.vizinhanca.dto.response.condominium.CondominiumSummaryResponse;
import com.tcc_vizinhanca.vizinhanca.dto.response.object.ObjectSummaryResponse;
import com.tcc_vizinhanca.vizinhanca.dto.response.publication.PublicationSummaryResponse;
import com.tcc_vizinhanca.vizinhanca.dto.response.resident.ResidentSummaryResponse;
import com.tcc_vizinhanca.vizinhanca.dto.response.service.ServiceSummaryResponse;
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
    private ReasonReportResponse reason_report;
    private ObjectSummaryResponse object_id;
    private ServiceSummaryResponse service;
    private PublicationSummaryResponse publication;

    public ReportDetailResponse(Report report) {
        this.id = report.getId();
        this.description = report.getDescription();
        this.resident = report.getResident() != null
                ? new ResidentSummaryResponse(report.getResident())
                : new ResidentSummaryResponse();
        this.reason_report = report.getReasonReport() != null
                ? new ReasonReportResponse(report.getReasonReport())
                : new ReasonReportResponse();
        this.object_id = report.getObject() != null
                ? new ObjectSummaryResponse(report.getObject())
                : new ObjectSummaryResponse();
        this.service = report.getService() != null
                ? new ServiceSummaryResponse(report.getService())
                : new ServiceSummaryResponse();
        this.publication = report.getPublication() != null
                ? new PublicationSummaryResponse(report.getPublication())
                : new PublicationSummaryResponse();
    }
}