/***************************************************
 * Objetivo: Mapper responsável pela conversão entre
 * os DTOs de requisição e a entidade Report,
 * utilizado nas operações de criação e atualização
 * Data: 15/05/2026
 * Autor: Leonardo Scotti
 * Versão: 1.0.05.26
 * *************************************************/

package com.tcc_vizinhanca.vizinhanca.mapper.report;

import com.tcc_vizinhanca.vizinhanca.dto.request.report.ReportRequest;
import com.tcc_vizinhanca.vizinhanca.entity.report.Report;

public class ReportMapper {

    public static Report toEntity(ReportRequest dto) {
        Report report = new Report();
        report.setDescription(dto.getDescription());
        return report;
    }

    public static Report updateEntity(ReportRequest dto, Report entity) {
        if (dto.getDescription() != null) {
            entity.setDescription(dto.getDescription());
        }
        return entity;
    }
}