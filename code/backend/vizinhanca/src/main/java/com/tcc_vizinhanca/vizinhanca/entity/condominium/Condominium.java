/***************************************************
 * Objetivo: Entidade responsável por representar a tabela de condomínios,
 * armazenando dados como nome, quantidade de blocos, apartamentos,
 * credenciais de acesso e informações do condomínio
 * Data: 24/04/2026
 * Autor: Leonardo Scotti
 * Versão: 1.0.04.26
 * *************************************************/

package com.tcc_vizinhanca.vizinhanca.entity.condominium;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDateTime;

@Entity
@Table(name = "tbl_condominio")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class Condominium {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id_condominio")
    private Long id;

    @Column(name = "nome", nullable = false)
    private String name;

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

    @Column(name = "data_criacao", insertable = false, updatable = false)
    private LocalDateTime creationDate;

}
