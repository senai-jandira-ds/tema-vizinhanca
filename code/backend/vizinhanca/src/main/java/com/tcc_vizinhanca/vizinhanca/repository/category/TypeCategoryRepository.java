/***************************************************
 * Objetivo: Repositório responsável pelo acesso e manipulação de dados
 * da entidade TypeCategory no banco de dados
 * Data: 24/04/2026
 * Autor: Leonardo Scotti
 * Versão: 1.0.04.26
 * *************************************************/

package com.tcc_vizinhanca.vizinhanca.repository.category;

import com.tcc_vizinhanca.vizinhanca.entity.category.TypeCategory;
import org.springframework.data.jpa.repository.JpaRepository;

public interface TypeCategoryRepository extends JpaRepository<TypeCategory, Long> {
}
