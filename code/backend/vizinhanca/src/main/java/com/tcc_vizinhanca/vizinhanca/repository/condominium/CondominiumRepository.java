/***************************************************
 * Objetivo: Repositório responsável pelo acesso e manipulação de dados
 * da entidade Condominium no banco de dados
 * Data: 24/04/2026
 * Autor: Leonardo Scotti
 * Versão: 1.0.04.26
 * *************************************************/

package com.tcc_vizinhanca.vizinhanca.repository.condominium;

import com.tcc_vizinhanca.vizinhanca.entity.condominium.Condominium;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.Optional;

public interface CondominiumRepository   extends JpaRepository<Condominium, Long> {

    @Query("SELECT DISTINCT c FROM Condominium c LEFT JOIN FETCH c.address WHERE c.email = :email")
    Optional<Condominium> findByEmail(String email);

    @Query("""
    SELECT DISTINCT c FROM Condominium c LEFT JOIN FETCH c.blocks LEFT JOIN FETCH c.residents r LEFT JOIN FETCH r.block
    LEFT JOIN FETCH c.services s LEFT JOIN FETCH s.category LEFT JOIN FETCH s.resident LEFT JOIN FETCH c.reports LEFT JOIN FETCH c.address
    WHERE c.email = :email
    """)
    Optional<Condominium> findDetailedByEmail(@Param("email") String email);

    boolean existsByCnpj(String cnpj);
    boolean existsByEmail(String email);

    @Query("SELECT DISTINCT c FROM Condominium c LEFT JOIN FETCH c.residents r LEFT JOIN FETCH r.block LEFT JOIN FETCH c.address WHERE c.id = :id")
    Optional<Condominium> findByIdWithDetails(@Param("id") Long id);
}
