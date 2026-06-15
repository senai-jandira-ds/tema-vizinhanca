package com.tcc_vizinhanca.vizinhanca.dto.response.report;

import com.tcc_vizinhanca.vizinhanca.dto.response.condominium.CondominiumSummaryResponse;
import com.tcc_vizinhanca.vizinhanca.dto.response.publication.PublicationSummaryResponse;
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
public class ReportSummaryResponse {

    private Long id;
    private String description;
    private ResidentSummaryResponse resident;
    private ReasonReportResponse reason_report;
    private Long object_id;
    private Long service_id;
    private PublicationSummaryResponse publication;

    public ReportSummaryResponse(Report report) {
        this.id = report.getId();
        this.description = report.getDescription();
        this.resident = report.getResident() != null
                ? new ResidentSummaryResponse(report.getResident())
                : new ResidentSummaryResponse();
        this.reason_report = report.getReasonReport() != null
                ? new ReasonReportResponse(report.getReasonReport())
                : new ReasonReportResponse();
        this.object_id = report.getObject() != null
                ? report.getObject().getId()
                : 0;
        this.service_id = report.getService() != null
                ? report.getService().getId()
                : 0;
        this.publication = report.getPublication() != null
                ? new PublicationSummaryResponse(report.getPublication())
                : new PublicationSummaryResponse();
    }
}
