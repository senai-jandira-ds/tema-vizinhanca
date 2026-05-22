/***************************************************
 * Objetivo: Entidade responsável por representar a tabela de condomínios,
 * armazenando dados como nome, quantidade de blocos, apartamentos,
 * credenciais de acesso e informações do condomínio
 * Data: 24/04/2026
 * Autor: Leonardo Scotti
 * Versão: 1.0.04.26
 * *************************************************/

package com.tcc_vizinhanca.vizinhanca.entity.condominium;

import com.tcc_vizinhanca.vizinhanca.entity.report.Report;
import com.tcc_vizinhanca.vizinhanca.entity.resident.Resident;
import com.tcc_vizinhanca.vizinhanca.entity.service.Service;
import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDate;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

@Entity
@Table(name = "tbl_condominio")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Condominium {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id_condominio")
    private Long id;

    @Column(name = "nome", nullable = false)
    private String name;

    @Column(name = "foto", columnDefinition = "TEXT")
    private String photo;

    @Column(name = "cnpj", nullable = false, unique = true)
    private String cnpj;

    @Column(name = "qtd_blocos", nullable = false)
    private Integer amountBlocks;

    @Column(name = "qtd_apto", nullable = false)
    private Integer amountApartments;

    @Column(name = "email", nullable = false, unique = true)
    private String email;

    @Column(name = "senha", nullable = false)
    private String password;

    @Column(name = "data_criacao", updatable = false)
    private LocalDate creationDate;

    @OneToMany(mappedBy = "condominium", fetch = FetchType.LAZY)
    private Set<Block> blocks = new HashSet<>();

    @OneToMany(mappedBy = "condominium", fetch = FetchType.LAZY)
    private Set<Resident> residents = new HashSet<>();

    @OneToMany(mappedBy = "condominium", fetch = FetchType.LAZY)
    private Set<Service> services = new HashSet<>();

    @OneToMany(mappedBy = "condominium", fetch = FetchType.LAZY)
    private Set<Report> reports = new HashSet<>();

    @OneToOne(mappedBy = "condominium", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    private CondominiumAddress address;

    @PrePersist
    public void prePersist() {
        this.creationDate = LocalDate.now();
    }
}
