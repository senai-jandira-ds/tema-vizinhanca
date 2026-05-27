/***************************************************
 * Objetivo: DTO de resposta responsável por encapsular os dados
 * completos de uma categoria, utilizado nas operações de busca,
 * criação e atualização
 * Data: 26/05/2026
 * Autor: Leonardo Scotti
 * Versão: 1.0.05.26
 * *************************************************/

package com.tcc_vizinhanca.vizinhanca.dto.response.category;

import com.tcc_vizinhanca.vizinhanca.entity.category.Category;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
public class CategoryDetailResponse {

    private Long id;
    private String name;
    private String description;
    private TypeCategoryResponse type_category;

    public CategoryDetailResponse(Category category) {
        this.id = category.getId();
        this.name = category.getName();
        this.description = category.getDescription();
        this.type_category = category.getTypeCategory() != null
                ? new TypeCategoryResponse(category.getTypeCategory())
                : new TypeCategoryResponse();
    }
}