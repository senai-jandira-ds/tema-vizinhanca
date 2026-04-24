/***************************************************
 * Objetivo: Repositório responsável pelo acesso e manipulação de dados
 * da entidade Condominium no banco de dados
 * Data: 24/04/2026
 * Autor: Leonardo Scotti
 * Versão: 1.0.04.26
 * *************************************************/

package com.tcc_vizinhanca.vizinhanca.repository;

import com.tcc_vizinhanca.vizinhanca.entity.Condominium;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface CondominiumRepository   extends JpaRepository<Condominium, Long> {

    Optional<Condominium> findByEmail(String email);
}
