/***************************************************
 * Objetivo: Entidade responsável por representar os tipos de categorias
 * da aplicação, permitindo organizar e diferenciar categorias de acordo
 * com seu contexto de uso, como objetos, serviços e outras classificações
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
@Table(name = "tbl_tipo_categoria")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class TypeCategory {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id_tipo_categoria")
    private Long id;

    @Column(name = "nome", length = 20, nullable = false)
    private String name;

}
