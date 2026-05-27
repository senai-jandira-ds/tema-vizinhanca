package com.tcc_vizinhanca.vizinhanca.dto.response.block;

import com.tcc_vizinhanca.vizinhanca.entity.condominium.Block;
import lombok.Getter;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
public class BlockListSummaryResponse {

    private Integer amount_blocks;
    private List<BlockSummaryResponse>  blocks;

    public BlockListSummaryResponse(List<Block> blocks) {
        this.amount_blocks = blocks.size();
        this.blocks = blocks.stream()
                .map(BlockSummaryResponse::new)
                .toList();
    }
}
