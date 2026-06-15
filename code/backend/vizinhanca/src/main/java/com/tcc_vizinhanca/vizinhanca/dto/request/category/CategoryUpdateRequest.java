/***************************************************
 * Objetivo: DTO responsável por receber os dados de atualização de uma categoria
 * Data: 26/05/2026
 * Autor: Leonardo Scotti
 * Versão: 1.0.05.26
 * *************************************************/

package com.tcc_vizinhanca.vizinhanca.dto.request.category;

import com.fasterxml.jackson.annotation.JsonProperty;
import jakarta.validation.constraints.Positive;
import jakarta.validation.constraints.Size;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class CategoryUpdateRequest {

    @Size(max = 50, message = "Nome deve ter no máximo 50 caracteres")
    private String name;

    @Size(max = 100, message = "Descrição deve ter no máximo 100 caracteres")
    private String description;

    @JsonProperty("type_category_id")
    @Positive(message = "ID do tipo de categoria deve ser positivo")
    private Long typeCategoryId;
}