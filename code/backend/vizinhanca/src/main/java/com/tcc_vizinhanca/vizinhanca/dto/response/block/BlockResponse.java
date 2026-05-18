/***************************************************
 * Objetivo: DTO responsável por retornar a lista de blocos
 * Data: 15/05/2026
 * Autor: Leonardo Scotti
 * Versão: 1.0.05.26
 * *************************************************/

package com.tcc_vizinhanca.vizinhanca.dto.response.block;

import com.tcc_vizinhanca.vizinhanca.entity.condominium.Block;
import lombok.Getter;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
public class BlockResponse {

    private Integer amountBlocks;
    private List<BlockDetailResponse> blocks;

    public BlockResponse(List<Block> blocks) {
        this.amountBlocks = blocks.size();
        this.blocks = blocks.stream()
                .map(BlockDetailResponse::new)
                .toList();
    }
}