/***************************************************
 * Objetivo: DTO responsável por retornar os dados resumidos de uma categoria
 * Data: 15/05/2026
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
public class CategorySummaryResponse {

    private Long id;
    private String name;
    private String description;
    private String typeCategory;

    public CategorySummaryResponse(Category category) {
        this.id = category.getId();
        this.name = category.getName();
        this.description = category.getDescription();
        this.typeCategory = category.getTypeCategory() != null
                ? category.getTypeCategory().getName()
                : null;
    }
}