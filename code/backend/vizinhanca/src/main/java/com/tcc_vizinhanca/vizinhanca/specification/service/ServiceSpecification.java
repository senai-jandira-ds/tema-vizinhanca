/***************************************************
 * Objetivo: Specification responsável pelos predicados de filtragem
 * da entidade Service, utilizados nas consultas dinâmicas com JPA
 * Data: 31/05/2026
 * Autor: Leonardo Scotti
 * Versão: 1.0.05.26
 * *************************************************/

package com.tcc_vizinhanca.vizinhanca.specification.service;

import com.tcc_vizinhanca.vizinhanca.entity.service.Service;
import org.springframework.data.jpa.domain.Specification;

import java.util.List;

public class ServiceSpecification {

    public static Specification<Service> hasCondominium(Long condominiumId) {
        return (root, query, cb) -> cb.equal(root.get("condominium").get("id"), condominiumId);
    }

    public static Specification<Service> hasResident(Long residentId) {
        return (root, query, cb) -> cb.equal(root.get("resident").get("id"), residentId);
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
