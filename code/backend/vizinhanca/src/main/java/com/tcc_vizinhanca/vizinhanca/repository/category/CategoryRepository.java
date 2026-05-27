/***************************************************
 * Objetivo: Repositório responsável pelo acesso e manipulação de dados
 * da entidade Category no banco de dados
 * Data: 24/04/2026
 * Autor: Leonardo Scotti
 * Versão: 1.0.04.26
 * *************************************************/

package com.tcc_vizinhanca.vizinhanca.repository.category;

import com.tcc_vizinhanca.vizinhanca.entity.category.Category;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface CategoryRepository extends JpaRepository<Category, Long> {

    List<Category> findByTypeCategoryId(Long typeCategoryId);

    @Query("SELECT DISTINCT c FROM Category c WHERE c.id IN (SELECT s.category.id FROM Service s WHERE s.condominium.id = :condominiumId) OR c.id IN (SELECT o.category.id FROM Object o WHERE o.condominium.id = :condominiumId)")
    List<Category> findByCondominiumId(@Param("condominiumId") Long condominiumId);

}
