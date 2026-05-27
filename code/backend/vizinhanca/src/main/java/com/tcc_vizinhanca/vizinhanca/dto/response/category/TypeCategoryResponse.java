/***************************************************
 * Objetivo: DTO de resposta responsável por encapsular os dados
 * de um tipo de categoria (dado pré-moldado no banco)
 * Data: 26/05/2026
 * Autor: Leonardo Scotti
 * Versão: 1.0.05.26
 * *************************************************/

package com.tcc_vizinhanca.vizinhanca.dto.response.category;

import com.tcc_vizinhanca.vizinhanca.entity.category.TypeCategory;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
public class TypeCategoryResponse {

    private Long id;
    private String name;

    public TypeCategoryResponse(TypeCategory typeCategory) {
        this.id = typeCategory.getId();
        this.name = typeCategory.getName();
    }
}