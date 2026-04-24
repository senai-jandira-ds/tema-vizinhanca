/***************************************************
 * Objetivo: Entidade responsável por representar as categorias da aplicação,
 * utilizadas para classificar objetos, serviços e outras funcionalidades,
 * permitindo organização e filtragem dos dados
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
@Table(name = "tbl_categoria")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class Category {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id_categoria")
    private Long id;

    @Column(name = "nome", length = 50, nullable = false)
    private String name;

    @Column(name = "descricao", nullable = false, length = 100)
    private String description;

    @ManyToOne
    @JoinColumn(name = "id_tipo_categoria")
    private TypeCategory typeCategory;

}
