/***************************************************
 * Objetivo: Entidade responsável por representar os serviços oferecidos
 * na aplicação, armazenando informações como imagem, título, tempo estimado,
 * nível de urgência, descrição, status e o relacionamento com morador,
 * categoria e condomínio
 * Data: 24/04/2026
 * Autor: Leonardo Scotti
 * Versão: 1.0.04.26
 * *************************************************/

package com.tcc_vizinhanca.vizinhanca.entity.service;

import com.tcc_vizinhanca.vizinhanca.entity.category.Category;
import com.tcc_vizinhanca.vizinhanca.entity.condominium.Condominium;
import com.tcc_vizinhanca.vizinhanca.entity.resident.Resident;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDateTime;

@Entity
@Table(name = "tbl_servico")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class Service {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id_servico")
    private Long id;

    @Column(name = "foto", nullable = false, columnDefinition = "TEXT")
    private String photo;

    @Column(name = "titulo", nullable = false, length = 100)
    private String title;

    @Column(name = "tempo_estimado", nullable = false)
    private Integer estimatedTime;

    @Column(name = "urgencia", nullable = false, length = 20)
    private String urgency;

    @Column(name = "descricao", nullable = false, columnDefinition = "TEXT")
    private String description;

    @Column(name = "data_criacao", columnDefinition = "DATETIME")
    private LocalDateTime creationDate;

    @Column(name = "status", nullable = false, length = 20)
    private String status;

    @ManyToOne
    @JoinColumn(name = "id_morador", nullable = false)
    private Resident resident;

    @ManyToOne
    @JoinColumn(name = "id_categoria", nullable = false)
    private Category category;

    @ManyToOne
    @JoinColumn(name = "id_condominio", nullable = false)
    private Condominium condominium;
}
