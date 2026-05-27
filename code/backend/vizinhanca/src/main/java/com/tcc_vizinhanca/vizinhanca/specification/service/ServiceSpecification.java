package com.tcc_vizinhanca.vizinhanca.specification.service;

import com.tcc_vizinhanca.vizinhanca.entity.service.Service;
import org.springframework.data.jpa.domain.Specification;

import java.util.List;

public class ServiceSpecification {

    public static Specification<Service> hasCondominium(Long condominiumId) {
        return (root, query, cb) -> cb.equal(root.get("condominium").get("id"), condominiumId);
    }

    public static Specification<Service> hasStatuses(List<String> statuses) {
        return (root, query, cb) -> root.get("status").in(statuses);
    }

    public static Specification<Service> hasCategories(List<Long> categoryIds) {
        return (root, query, cb) -> root.get("category").get("id").in(categoryIds);
    }

    public static Specification<Service> hasBlocks(List<Long> blockIds) {
        return (root, query, cb) -> root.get("resident").get("block").get("id").in(blockIds);
    }

}
