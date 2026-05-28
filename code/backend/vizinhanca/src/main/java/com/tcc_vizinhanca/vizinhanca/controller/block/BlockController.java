/***************************************************
 * Objetivo: Controlador responsável por expor os endpoints
 * da entidade Block, gerenciando as requisições HTTP
 * de listagem, busca, criação, atualização e remoção
 * Data: 28/05/2026
 * Autor: Leonardo Scotti
 * Versão: 1.0.05.26
 * *************************************************/

package com.tcc_vizinhanca.vizinhanca.controller.block;

import com.tcc_vizinhanca.vizinhanca.dto.request.block.BlockCreateRequest;
import com.tcc_vizinhanca.vizinhanca.dto.request.block.BlockUpdateRequest;
import com.tcc_vizinhanca.vizinhanca.dto.response.ApiResponse;
import com.tcc_vizinhanca.vizinhanca.dto.response.block.BlockDetailResponse;
import com.tcc_vizinhanca.vizinhanca.dto.response.block.BlockResponse;
import com.tcc_vizinhanca.vizinhanca.entity.condominium.Block;
import com.tcc_vizinhanca.vizinhanca.security.jwt.AuthenticatedUser;
import com.tcc_vizinhanca.vizinhanca.service.block.BlockService;
import com.tcc_vizinhanca.vizinhanca.util.ResponseUtil;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/v1/block")
@Tag(name = "Block", description = "Endpoints para gerenciamento dos blocos.")
public class BlockController {

    @Autowired
    private BlockService blockService;

    // GET ALL
    @GetMapping
    public ResponseEntity<ApiResponse<BlockResponse>> listAllBlocks() {
        List<Block> blocks = blockService.getSelectAllBlocks();
        BlockResponse response = new BlockResponse(blocks);

        return ResponseEntity.ok(ResponseUtil.success(response, "Lista de blocos retornada com sucesso!"));
    }

    // GET BY ID
    @GetMapping("/{id}")
    public ResponseEntity<ApiResponse<BlockDetailResponse>> searchBlockById(@PathVariable Long id) {
        Block block = blockService.getSelectBlockById(id);
        BlockDetailResponse response = new BlockDetailResponse(block);

        return ResponseEntity.ok(ResponseUtil.success(response, "Bloco encontrado com sucesso!"));
    }

    // POST
    @PostMapping
    public ResponseEntity<ApiResponse<BlockDetailResponse>> insertBlock(
            @Valid @RequestBody BlockCreateRequest blockCreateRequest,
            HttpServletRequest request) {

        AuthenticatedUser user =
                (AuthenticatedUser) SecurityContextHolder.getContext().getAuthentication().getPrincipal();

        Block block = new Block();
        block.setBlock(blockCreateRequest.getBlock());

        Block newBlock = blockService.setInsertBlock(block, user.email());
        BlockDetailResponse response = new BlockDetailResponse(newBlock);

        return ResponseEntity.status(HttpStatus.CREATED)
                .body(ResponseUtil.success(response, "Bloco criado com sucesso!"));
    }

    // PUT
    @PutMapping("/{id}")
    public ResponseEntity<ApiResponse<BlockDetailResponse>> updateBlock(
            @PathVariable Long id,
            @Valid @RequestBody BlockUpdateRequest blockUpdateRequest) {

        Block block = new Block();
        block.setBlock(blockUpdateRequest.getBlock());

        Block updatedBlock = blockService.setUpdateBlock(block, id);
        BlockDetailResponse response = new BlockDetailResponse(updatedBlock);

        return ResponseEntity.ok(ResponseUtil.success(response, "Bloco atualizado com sucesso!"));
    }

    // DELETE
    @DeleteMapping("/{id}")
    public ResponseEntity<ApiResponse<Void>> deleteBlock(@PathVariable Long id) {
        blockService.setDeleteBlock(id);
        return ResponseEntity.ok(ResponseUtil.success(null, "Bloco deletado com sucesso!"));
    }
}