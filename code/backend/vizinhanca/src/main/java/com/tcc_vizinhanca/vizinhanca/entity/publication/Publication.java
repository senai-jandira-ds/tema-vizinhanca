/***************************************************
 * Objetivo: Entidade responsável por representar as publicações realizadas na aplicação,
 * armazenando informações como imagem, título, descrição e o relacionamento
 * com o morador autor da publicação e o condomínio associado
 * Data: 24/04/2026
 * Autor: Leonardo Scotti
 * Versão: 1.0.04.26
 * *************************************************/

package com.tcc_vizinhanca.vizinhanca.entity.publication;

import com.tcc_vizinhanca.vizinhanca.entity.resident.Resident;
import com.tcc_vizinhanca.vizinhanca.entity.condominium.Condominium;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Table(name = "tbl_publicacao")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class Publication {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id_publicacao")
    private Long id;

    @Column(name = "foto", nullable = false, columnDefinition = "TEXT")
    private String photo;

    @Column(name = "titulo", length = 120, nullable = false)
    private String title;

    @Column(name = "descricao", columnDefinition = "TEXT", nullable = false)
    private String description;

    @ManyToOne
    @JoinColumn(name = "id_morador")
    private Resident resident;

    @ManyToOne
    @JoinColumn(name = "id_condominio")
    private Condominium condominium;

}
