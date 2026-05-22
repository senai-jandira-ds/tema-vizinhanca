/***************************************************
 * Objetivo: Repositório responsável pelo acesso e manipulação de dados
 * da entidade ReasonReport (motivos de denúncia) no banco de dados
 * Data: 24/04/2026
 * Autor: Leonardo Scotti
 * Versão: 1.0.04.26
 * *************************************************/

package com.tcc_vizinhanca.vizinhanca.repository.report;

import com.tcc_vizinhanca.vizinhanca.entity.report.ReasonReport;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ReasonReportRepository extends JpaRepository<ReasonReport, Long> {
}
