package com.tcc_vizinhanca.vizinhanca.repository.resident;

import com.tcc_vizinhanca.vizinhanca.entity.condominium.ActivityView;
import com.tcc_vizinhanca.vizinhanca.entity.resident.RankingView;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface RankingViewRepository extends JpaRepository<RankingView, Integer> {

    @Query(value = "SELECT * FROM vw_ranking_geral WHERE id_condominio = :id ORDER BY data_criacao DESC", nativeQuery = true)
    List<ActivityView> findByIdCondominium(@Param("id") Long id);
}
