/***************************************************
 * Objetivo: Repositório responsável pelo acesso e manipulação de dados
 * da entidade Resident no banco de dados
 * Data: 24/04/2026
 * Autor: Leonardo Scotti
 * Versão: 1.0.04.26
 * *************************************************/

package com.tcc_vizinhanca.vizinhanca.repository.resident;

import com.tcc_vizinhanca.vizinhanca.entity.resident.Resident;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface ResidentRepository extends JpaRepository<Resident,Long> {

    Optional<Resident> findByEmail(String email);
    boolean existsByCpf(String cpf);
    boolean existsByEmail(String email);
}
