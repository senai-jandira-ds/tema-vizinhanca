/***************************************************
 * Objetivo: Entidade responsável por representar os motivos de denúncia
 * na aplicação, permitindo classificar e padronizar as denúncias realizadas
 * pelos usuários
 * Data: 24/04/2026
 * Autor: Leonardo Scotti
 * Versão: 1.0.04.26
 * *************************************************/

package com.tcc_vizinhanca.vizinhanca.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Table(name = "tbl_motivo_denuncia")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class ReasonReport {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id_motivo_denuncia")
    private Long id;

    @Column(name = "nome", length = 50, nullable = false)
    private String name;

}
