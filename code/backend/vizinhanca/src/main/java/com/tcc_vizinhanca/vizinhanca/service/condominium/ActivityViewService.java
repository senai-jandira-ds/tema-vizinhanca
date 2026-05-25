package com.tcc_vizinhanca.vizinhanca.service.condominium;

import com.tcc_vizinhanca.vizinhanca.entity.condominium.ActivityView;
import com.tcc_vizinhanca.vizinhanca.repository.condominium.ActivityViewRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class ActivityViewService {

    @Autowired
    private ActivityViewRepository activityViewRepository;

    // SELECT ALL
    @Cacheable(value = "activities", key = "#idCondominium")
    public List<ActivityView> getSelectActivitiesViewByCondominiumId(Long idCondominium){
        return activityViewRepository.findByIdCondominium(idCondominium);
    }

    @CacheEvict(value = "activities", key = "#idCondominium")
    public void evictCache(Long idCondominium) {
    }

}
