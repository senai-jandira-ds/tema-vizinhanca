/***************************************************
 * Objetivo: Entidade responsável por representar as denúncias realizadas
 * na aplicação, armazenando a descrição da denúncia, o morador responsável,
 * o motivo e a associação opcional com objeto, serviço ou publicação
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
@Table(name = "tbl_denuncia")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class Report {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id_denuncia")
    private Long id;

    @Column(name = "descricao", columnDefinition = "TEXT", nullable = false)
    private String description;

    @ManyToOne
    @JoinColumn(name = "id_morador", nullable = false)
    private Resident resident;

    @ManyToOne
    @JoinColumn(name = "id_motivo_denuncia", nullable = false)
    private ReasonReport reasonReport;

    @ManyToOne
    @JoinColumn(name = "id_objeto")
    private Object object;

    @ManyToOne
    @JoinColumn(name = "id_servico")
    private Service service;

    @ManyToOne
    @JoinColumn(name = "id_publicacao")
    private Publication publication;
}
