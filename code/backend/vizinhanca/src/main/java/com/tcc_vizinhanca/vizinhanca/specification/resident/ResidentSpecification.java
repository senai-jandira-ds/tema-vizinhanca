package com.tcc_vizinhanca.vizinhanca.specification.resident;

import com.tcc_vizinhanca.vizinhanca.entity.resident.Resident;
import org.springframework.data.jpa.domain.Specification;

import java.util.List;

public class ResidentSpecification {

    public static Specification<Resident> hasCondominium(Long condominiumId) {
        return (root, query, cb) -> cb.equal(root.get("condominium").get("id"), condominiumId);
    }

    public static Specification<Resident> hasBlocks(List<Long> blockIds) {
        return (root, query, cb) -> root.get("block").get("id").in(blockIds);
    }

    public static Specification<Resident> isActive(Boolean isActive) {
        return (root, query, cb) -> cb.equal(root.get("isActive"), isActive);
    }

}
