/***************************************************
 * Objetivo: Entidade responsável por representar a tabela de moradores,
 * armazenando dados pessoais, endereço no condomínio, credenciais
 * de acesso e informações relacionadas ao residente
 * Data: 24/04/2026
 * Autor: Leonardo Scotti
 * Versão: 1.0.04.26
 * *************************************************/

package com.tcc_vizinhanca.vizinhanca.entity.resident;

import com.tcc_vizinhanca.vizinhanca.entity.condominium.Condominium;
import com.tcc_vizinhanca.vizinhanca.entity.publication.Publication;
import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDate;
import java.util.List;

@Entity
@Table(name = "tbl_morador")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Resident {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id_morador")
    private Long id;

    @Column(name = "foto")
    private String photo;

    @Column(name = "nome", nullable = false, length = 120)
    private String name;

    @Column(name = "apto", nullable = false, length = 10)
    private String apartment;

    @Column(name = "bloco", nullable = false, length = 10)
    private String block;

    @Column(name = "cpf", nullable = false, unique = true, length = 11)
    private String cpf;

    @Column(name = "email", nullable = false, length = 120, unique = true)
    private String email;

    @Column(name = "senha", nullable = false)
    private String password;

    @Column(name = "telefone", nullable = false, length = 11)
    private String phone;

    @Column(name = "pontuacao", nullable = false)
    private Integer score;

    @Column(name = "is_active", nullable = false)
    private Boolean isActive;

    @Column(name = "data_criacao", nullable = false)
    private LocalDate creationDate;

    @OneToMany(mappedBy = "resident",  fetch = FetchType.LAZY)
    private List<Publication> publications;

    @ManyToOne
    @JoinColumn( name = "id_condominio")
    private Condominium condominium;

    @PrePersist
    public void prePersist() {
        this.creationDate = LocalDate.now();
    }
}
