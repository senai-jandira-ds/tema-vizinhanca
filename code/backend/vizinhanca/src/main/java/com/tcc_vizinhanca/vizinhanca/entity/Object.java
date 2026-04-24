/***************************************************
 * Objetivo: Entidade responsável por representar os objetos disponibilizados
 * na aplicação, armazenando informações como imagem, título, descrição,
 * tempo de disponibilidade, status e o relacionamento com o morador,
 * categoria e condomínio
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

import java.time.LocalDate;
import java.time.LocalDateTime;

@Entity
@Table(name = "tbl_objeto")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class Object {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id_objeto")
    private Long id;

    @Column(name = "foto", columnDefinition = "TEXT", nullable = false)
    private String photo;

    @Column(name = "titulo", length = 120, nullable = false)
    private String title;

    @Column(name = "data_limite", nullable = false)
    private LocalDate deadline;

    @Column(name = "descricao", nullable = false, columnDefinition = "TEXT")
    private String description;

    @Column(name = "data_criacao", insertable = false, updatable = false)
    private LocalDateTime creationDate;

    @Column(name = "status", nullable = false, length = 20)
    private String status;

    @ManyToOne
    @JoinColumn(name = "id_morador")
    private Resident resident;

    @ManyToOne
    @JoinColumn(name = "id_categoria")
    private Category category;

    @ManyToOne
    @JoinColumn(name = "id_condominio")
    private Condominium condominium;

}
