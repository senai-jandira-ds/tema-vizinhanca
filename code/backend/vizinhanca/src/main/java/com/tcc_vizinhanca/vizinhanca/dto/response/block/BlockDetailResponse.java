/***************************************************
 * Objetivo: DTO responsável por retornar os dados de um bloco
 * Data: 15/05/2026
 * Autor: Leonardo Scotti
 * Versão: 1.0.05.26
 * *************************************************/

package com.tcc_vizinhanca.vizinhanca.dto.response.block;

import com.tcc_vizinhanca.vizinhanca.dto.response.condominium.CondominiumSummaryResponse;
import com.tcc_vizinhanca.vizinhanca.entity.condominium.Block;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
public class BlockDetailResponse {

    private Long id;
    private String block;
    private CondominiumSummaryResponse condominium;

    public BlockDetailResponse(Block block) {
        this.id = block.getId();
        this.block = block.getBlock();
        this.condominium = new CondominiumSummaryResponse(block.getCondominium());
    }
}