/***************************************************
 * Objetivo: Entidade responsável por representar as notificações da aplicação,
 * armazenando título, data de criação, status de visualização e a referência
 * de origem da notificação, além do relacionamento com o morador destinatário
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

@Entity
@Table(name = "tbl_notificacoes")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class Notification {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id_notificacao")
    private Long id;

    @Column(name = "titulo", nullable = false, length = 100)
    private String title;

    @Column(name = "data_criacao", nullable = false)
    private LocalDate creationDate;

    @Column(name = "visto", nullable = false)
    private Boolean seen;

    @Column(name = "tipo_origem", nullable = false, length = 20)
    private String originType;

    @Column(name = "id_origem", nullable = false)
    private Long originId;

    @ManyToOne
    @JoinColumn(name = "id_morador", nullable = false)
    private Resident resident;
}
