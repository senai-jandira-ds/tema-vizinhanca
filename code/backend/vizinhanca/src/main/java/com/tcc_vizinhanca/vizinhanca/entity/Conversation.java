/***************************************************
 * Objetivo: Entidade responsável por representar as conversas entre usuários,
 * armazenando informações como data de criação, status de visualização
 * e o relacionamento entre o iniciador e o receptor da conversa
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

import java.time.LocalDateTime;

@Entity
@Table(name = "tbl_conversa")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class Conversation {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id_conversa")
    private Long id;

    @Column(name = "data_criacao", nullable = false)
    private LocalDateTime createdAt;

    @ManyToOne
    @JoinColumn(name = "id_iniciador", nullable = false)
    private Resident initiator;

    @ManyToOne
    @JoinColumn(name = "id_receptor", nullable = false)
    private Resident receiver;
}
