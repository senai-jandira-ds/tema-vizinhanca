package com.tcc_vizinhanca.vizinhanca.specification.object;

import org.springframework.data.jpa.domain.Specification;
import com.tcc_vizinhanca.vizinhanca.entity.object.Object;

import java.util.List;

public class ObjectSpecification {

    public static Specification<Object> hasCondominium(Long condominiumId) {
        return (root, query, cb) -> cb.equal(root.get("condominium").get("id"), condominiumId);
    }

    public static Specification<Object> hasStatuses(List<String> statuses) {
        return (root, query, cb) -> root.get("status").in(statuses);
    }

    public static Specification<Object> hasCategories(List<Long> categoryIds) {
        return (root, query, cb) -> root.get("category").get("id").in(categoryIds);
    }

    public static Specification<Object> hasBlocks(List<Long> blockIds) {
        return (root, query, cb) -> root.get("resident").get("block").get("id").in(blockIds);
    }

}
