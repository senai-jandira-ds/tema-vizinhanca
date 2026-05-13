package com.tcc_vizinhanca.vizinhanca.repository.condominium;

import com.tcc_vizinhanca.vizinhanca.entity.condominium.ActivityView;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface ActivityViewRepository extends JpaRepository<ActivityView, Long> {

    @Query(value = "SELECT a FROM ActivityView a WHERE a.idCondominium = :id")
    List<ActivityView> findByIdCondominium(@Param("id") Long id);
}
