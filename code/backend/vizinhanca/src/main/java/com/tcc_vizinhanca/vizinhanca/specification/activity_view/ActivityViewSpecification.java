package com.tcc_vizinhanca.vizinhanca.specification.activity_view;

import com.tcc_vizinhanca.vizinhanca.entity.condominium.ActivityView;
import org.springframework.data.jpa.domain.Specification;

import java.util.List;

public class ActivityViewSpecification {

    public static Specification<ActivityView> hasCondominium(Long condominiumId) {
        return (root, query, cb) -> cb.equal(root.get("idCondominium"), condominiumId);
    }

    public static Specification<ActivityView> hasStatus(String status) {
        return (root, query, cb) -> cb.equal(root.get("status"), status);
    }

    public static Specification<ActivityView> hasType(String type) {
        return (root, query, cb) -> cb.equal(root.get("type"), type);
    }

    public static Specification<ActivityView> hasResidents(List<Long> residentIds) {
        return (root, query, cb) -> root.get("idMorador").in(residentIds);
    }

}
