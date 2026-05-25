/***************************************************
 * Objetivo: Controlador responsável por expor os endpoints
 * da entidade Report, gerenciando as requisições HTTP
 * de listagem, busca, criação, atualização e remoção
 * Data: 15/05/2026
 * Autor: Leonardo Scotti
 * Versão: 1.0.05.26
 * *************************************************/

package com.tcc_vizinhanca.vizinhanca.controller.report;

import com.tcc_vizinhanca.vizinhanca.dto.request.report.ReportRequest;
import com.tcc_vizinhanca.vizinhanca.dto.response.ApiResponse;
import com.tcc_vizinhanca.vizinhanca.dto.response.report.ReportDetailResponse;
import com.tcc_vizinhanca.vizinhanca.dto.response.report.ReportResponse;
import com.tcc_vizinhanca.vizinhanca.entity.report.Report;
import com.tcc_vizinhanca.vizinhanca.mapper.report.ReportMapper;
import com.tcc_vizinhanca.vizinhanca.security.jwt.AuthenticatedUser;
import com.tcc_vizinhanca.vizinhanca.security.jwt.JwtService;
import com.tcc_vizinhanca.vizinhanca.service.report.ReportService;
import com.tcc_vizinhanca.vizinhanca.util.ResponseUtil;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/v1/report")
@Tag(name = "Report", description = "Endpoints para gerenciamento das denúncias.")
public class ReportController {

    @Autowired
    private ReportService reportService;

    @Autowired
    private JwtService jwtService;

    // GET ALL
    @GetMapping
    public ResponseEntity<ApiResponse<ReportResponse>> listAllReports() {
        List<Report> reports = reportService.getSelectAllReports();

        ReportResponse response = new ReportResponse(reports);

        return ResponseEntity.ok(ResponseUtil.success(response, "Lista de denúncias retornada com sucesso!"));
    }

    // GET BY ID
    @GetMapping("/{id}")
    public ResponseEntity<ApiResponse<ReportDetailResponse>> searchReportById(@PathVariable Long id) {
        Report report = reportService.getSelectReportById(id);

        ReportDetailResponse response = new ReportDetailResponse(report);

        return ResponseEntity.ok(ResponseUtil.success(response, "Denúncia encontrada com sucesso!"));
    }

    // POST
    @PostMapping
    public ResponseEntity<ApiResponse<ReportDetailResponse>> insertReport(
            @Valid @RequestBody ReportRequest reportRequest,
            HttpServletRequest request) {

        AuthenticatedUser user =
                (AuthenticatedUser)
                        SecurityContextHolder
                                .getContext()
                                .getAuthentication()
                                .getPrincipal();

        Report report = ReportMapper.toEntity(reportRequest);

        Report newReport = reportService.setInsertReport(
                report,
                user.email(),
                reportRequest.getReasonReportId(),
                reportRequest.getObjectId(),
                reportRequest.getServiceId(),
                reportRequest.getPublicationId()
        );

        ReportDetailResponse response = new ReportDetailResponse(newReport);

        return ResponseEntity.status(HttpStatus.CREATED)
                .body(ResponseUtil.success(response, "Denúncia criada com sucesso!"));
    }

    // PUT
    @PutMapping("/{id}")
    public ResponseEntity<ApiResponse<ReportDetailResponse>> updateReport(
            @Valid @RequestBody ReportRequest reportRequest,
            @PathVariable Long id) {

        Report existing = reportService.getSelectReportById(id);
        Report report = ReportMapper.updateEntity(reportRequest, existing);

        Report updatedReport = reportService.setUpdateReport(report, id);

        ReportDetailResponse response = new ReportDetailResponse(updatedReport);

        return ResponseEntity.ok(ResponseUtil.success(response, "Denúncia atualizada com sucesso!"));
    }

    // DELETE
    @DeleteMapping("/{id}")
    public ResponseEntity<ApiResponse<Void>> deleteReport(@PathVariable Long id) {
        reportService.setDeleteReport(id);

        return ResponseEntity.ok(ResponseUtil.success(null, "Denúncia deletada com sucesso!"));
    }
}