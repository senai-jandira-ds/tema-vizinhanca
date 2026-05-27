package com.tcc_vizinhanca.vizinhanca.service.condominium;

import com.tcc_vizinhanca.vizinhanca.entity.condominium.ActivityView;
import com.tcc_vizinhanca.vizinhanca.repository.condominium.ActivityViewRepository;
import com.tcc_vizinhanca.vizinhanca.repository.resident.ResidentRepository;
import com.tcc_vizinhanca.vizinhanca.service.resident.ResidentService;
import com.tcc_vizinhanca.vizinhanca.specification.activity_view.ActivityViewSpecification;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class ActivityViewService {

    @Autowired
    private ActivityViewRepository activityViewRepository;

    @Autowired
    private ResidentRepository residentRepository;

    // SELECT ALL
    @Cacheable(value = "activities", key = "#idCondominium")
    public List<ActivityView> getSelectActivitiesViewByCondominiumId(Long idCondominium){
        return activityViewRepository.findByIdCondominium(idCondominium);
    }

    // SELECT WITH FILTERS
    public Page<ActivityView> getSelectActivitiesByFilters(
            Long condominiumId,
            String status,
            String type,
            Long blockId,
            Pageable pageable) {

        Specification<ActivityView> spec = Specification
                .where(ActivityViewSpecification.hasCondominium(condominiumId));

        if (status != null) {
            spec = spec.and(ActivityViewSpecification.hasStatus(status));
        }

        if (type != null) {
            spec = spec.and(ActivityViewSpecification.hasType(type));
        }

        if (blockId != null) {
            List<Long> residentIds = residentRepository
                    .findIdsByCondominiumIdAndBlockId(condominiumId, blockId);
            spec = spec.and(ActivityViewSpecification.hasResidents(residentIds));
        }

        return activityViewRepository.findAll(spec, pageable);
    }

    @CacheEvict(value = "activities", key = "#idCondominium")
    public void evictCache(Long idCondominium) {
    }

}
