/***************************************************
 * Objetivo: DTO responsável por receber os dados de criação de um bloco
 * Data: 15/05/2026
 * Autor: Leonardo Scotti
 * Versão: 1.0.05.26
 * *************************************************/

package com.tcc_vizinhanca.vizinhanca.dto.request.block;

import jakarta.validation.constraints.NotBlank;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class BlockCreateRequest {

    @NotBlank(message = "Nome do bloco é obrigatório")
    private String block;
}