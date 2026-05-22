package com.tcc_vizinhanca.vizinhanca.service.condominium;

import com.tcc_vizinhanca.vizinhanca.entity.condominium.ActivityView;
import com.tcc_vizinhanca.vizinhanca.repository.condominium.ActivityViewRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class ActivityViewService {

    @Autowired
    private ActivityViewRepository activityViewRepository;

    // SELECT ALL
    public List<ActivityView> getSelectActivitiesViewByCondominiumId(Long idCondominio){
        System.out.println(activityViewRepository.findByIdCondominium(idCondominio).toString() + " " + idCondominio );
        return activityViewRepository.findByIdCondominium(idCondominio);
    }

}
