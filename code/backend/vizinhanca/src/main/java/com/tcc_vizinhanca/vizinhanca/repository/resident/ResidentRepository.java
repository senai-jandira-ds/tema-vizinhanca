/***************************************************
 * Objetivo: Repositório responsável pelo acesso e manipulação de dados
 * da entidade Resident no banco de dados
 * Data: 24/04/2026
 * Autor: Leonardo Scotti
 * Versão: 1.0.04.26
 * *************************************************/

package com.tcc_vizinhanca.vizinhanca.repository.resident;

import com.tcc_vizinhanca.vizinhanca.entity.resident.Resident;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;

public interface ResidentRepository extends JpaRepository<Resident,Long> {

    @Query("SELECT DISTINCT r FROM Resident r LEFT JOIN FETCH r.publications p WHERE r.email = :email")
    Optional<Resident> findByEmailWithPublications(@Param("email") String email);

    List<Resident> findByCondominiumId(Long condominiumId);

    Optional<Resident> findByEmail(String email);

    Page<Resident> findByCondominiumId(Long condominiumId, Pageable pageable);

    Page<Resident> findAll(Pageable pageable);

    boolean existsByCpf(String cpf);
    boolean existsByEmail(String email);
}
