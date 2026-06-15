package com.tcc_vizinhanca.vizinhanca.specification.report;

import com.tcc_vizinhanca.vizinhanca.entity.report.Report;
import org.springframework.data.jpa.domain.Specification;

import java.util.List;

public class ReportSpecification {

    public static Specification<Report> hasCondominium(Long condominiumId) {
        return (root, query, cb) -> cb.equal(root.get("condominium").get("id"), condominiumId);
    }

    public static Specification<Report> hasStatuses(List<String> statuses) {
        return (root, query, cb) -> root.get("status").in(statuses);
    }

    public static Specification<Report> hasReasons(List<Long> reasonIds) {
        return (root, query, cb) -> root.get("reasonReport").get("id").in(reasonIds);
    }

    public static Specification<Report> hasBlocks(List<Long> blockIds) {
        return (root, query, cb) -> root.get("resident").get("block").get("id").in(blockIds);
    }

}
