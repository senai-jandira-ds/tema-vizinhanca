/***************************************************
 * Objetivo: Repositório responsável pelo acesso e manipulação de dados
 * da entidade Service no banco de dados
 * Data: 24/04/2026
 * Autor: Leonardo Scotti
 * Versão: 1.0.04.26
 * *************************************************/

package com.tcc_vizinhanca.vizinhanca.repository.service;

import com.tcc_vizinhanca.vizinhanca.entity.service.Service;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface ServiceRepository extends JpaRepository<Service, Long> {

    List<Service> findByCondominiumId(Long condominiumId);

    List<Service> findByResidentId(Long residentId);

    @Query("SELECT s FROM Service s WHERE s.condominium.id = :condominiumId AND s.status = :status")
    List<Service> findByCondominiumIdAndStatus(@Param("condominiumId") Long condominiumId, @Param("status") String status);

    @Query("SELECT s FROM Service s WHERE s.condominium.id = :condominiumId AND s.category.id = :categoryId")
    List<Service> findByCondominiumIdAndCategoryId(@Param("condominiumId") Long condominiumId, @Param("categoryId") Long categoryId);
}
