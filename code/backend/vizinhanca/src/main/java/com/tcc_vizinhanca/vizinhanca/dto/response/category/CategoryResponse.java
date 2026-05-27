/***************************************************
 * Objetivo: DTO de resposta responsável por encapsular a listagem
 * de categorias retornada nas requisições de consulta geral
 * Data: 26/05/2026
 * Autor: Leonardo Scotti
 * Versão: 1.0.05.26
 * *************************************************/

package com.tcc_vizinhanca.vizinhanca.dto.response.category;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonPropertyOrder;
import com.tcc_vizinhanca.vizinhanca.entity.category.Category;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
@NoArgsConstructor
@JsonPropertyOrder({"amount_categories", "categories"})
public class CategoryResponse {

    @JsonProperty("amount_categories")
    public Integer getAmountCategories() {
        return categories != null ? categories.size() : 0;
    }

    private List<CategoryDetailResponse> categories;

    public CategoryResponse(List<Category> categories) {
        this.categories = categories.stream()
                .map(CategoryDetailResponse::new)
                .toList();
    }
}