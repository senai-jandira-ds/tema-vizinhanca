/***************************************************
 * Objetivo: Repositório responsável pelo acesso e manipulação de dados
 * da entidade Object no banco de dados
 * Data: 24/04/2026
 * Autor: Leonardo Scotti
 * Versão: 1.0.04.26
 * *************************************************/

package com.tcc_vizinhanca.vizinhanca.repository.object;

import com.tcc_vizinhanca.vizinhanca.entity.object.Object;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public interface ObjectRepository extends JpaRepository<Object, Long> {

    Page<Object> findByCondominiumId(Long condominiumId, Pageable pageable);

    Page<Object> findByResidentId(Long residentId, Pageable pageable);

    @Query("SELECT o FROM Object o WHERE o.condominium.id = :condominiumId AND o.status = :status")
    Page<Object> findByCondominiumIdAndStatus(
            @Param("condominiumId") Long condominiumId,
            @Param("status") String status,
            Pageable pageable);

    @Query("SELECT o FROM Object o WHERE o.condominium.id = :condominiumId AND o.category.id = :categoryId")
    Page<Object> findByCondominiumIdAndCategoryId(
            @Param("condominiumId") Long condominiumId,
            @Param("categoryId") Long categoryId,
            Pageable pageable);

}
