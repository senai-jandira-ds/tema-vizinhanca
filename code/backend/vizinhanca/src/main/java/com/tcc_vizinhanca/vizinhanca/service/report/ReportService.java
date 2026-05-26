/***************************************************
 * Objetivo: Serviço responsável pela regra de negócio
 * das denúncias, gerenciando operações de consulta,
 * inserção, atualização e remoção no banco de dados
 * Data: 15/05/2026
 * Autor: Leonardo Scotti
 * Versão: 1.0.05.26
 * *************************************************/

package com.tcc_vizinhanca.vizinhanca.service.report;

import com.tcc_vizinhanca.vizinhanca.entity.report.ReasonReport;
import com.tcc_vizinhanca.vizinhanca.entity.report.Report;
import com.tcc_vizinhanca.vizinhanca.entity.resident.Resident;
import com.tcc_vizinhanca.vizinhanca.repository.object.ObjectRepository;
import com.tcc_vizinhanca.vizinhanca.repository.publication.PublicationRepository;
import com.tcc_vizinhanca.vizinhanca.repository.report.ReasonReportRepository;
import com.tcc_vizinhanca.vizinhanca.repository.report.ReportRepository;
import com.tcc_vizinhanca.vizinhanca.repository.resident.ResidentRepository;
import com.tcc_vizinhanca.vizinhanca.repository.service.ServiceRepository;
import lombok.NonNull;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import java.util.List;

@Service
public class ReportService {

    @Autowired
    private ReportRepository reportRepository;

    @Autowired
    private ResidentRepository residentRepository;

    @Autowired
    private ReasonReportRepository reasonReportRepository;

    @Autowired
    private ObjectRepository objectRepository;

    @Autowired
    private ServiceRepository serviceRepository;

    @Autowired
    private PublicationRepository publicationRepository;

    // SELECT ALL
    public Page<Report> getSelectAllReports(Pageable pageable) {
        return reportRepository.findAll(pageable);
    }

    // SELECT BY ID
    public Report getSelectReportById(Long id) {
        return reportRepository.findById(id)
                .orElseThrow(() -> new ResponseStatusException(
                        HttpStatus.NOT_FOUND, "Denúncia não encontrada no banco de dados!"));
    }

    // INSERT REPORT
    public Report setInsertReport(@NonNull Report report, String email,
                                  Long reasonReportId, Long objectId,
                                  Long serviceId, Long publicationId) {

        Resident resident = residentRepository.findByEmail(email)
                .orElseThrow(() -> new ResponseStatusException(
                        HttpStatus.NOT_FOUND, "Morador não encontrado!"));

        ReasonReport reasonReport = reasonReportRepository.findById(reasonReportId)
                .orElseThrow(() -> new ResponseStatusException(
                        HttpStatus.NOT_FOUND, "Motivo de denúncia não encontrado!"));

        report.setId(null);
        report.setResident(resident);
        report.setReasonReport(reasonReport);

        if (objectId != null) report.setObject(objectRepository.getReferenceById(objectId));
        if (serviceId != null) report.setService(serviceRepository.getReferenceById(serviceId));
        if (publicationId != null) report.setPublication(publicationRepository.getReferenceById(publicationId));

        return reportRepository.save(report);
    }

    // UPDATE REPORT
    public Report setUpdateReport(@NonNull Report report, Long idReport) {
        Report existingReport = getSelectReportById(idReport);

        BeanUtils.copyProperties(report, existingReport,
                "id", "resident", "reasonReport", "object", "service", "publication");

        return reportRepository.save(existingReport);
    }

    // DELETE REPORT
    public void setDeleteReport(Long idReport) {
        if (!reportRepository.existsById(idReport)) {
            throw new ResponseStatusException(
                    HttpStatus.NOT_FOUND, "Denúncia não encontrada no banco de dados!");
        }
        reportRepository.deleteById(idReport);
    }
}