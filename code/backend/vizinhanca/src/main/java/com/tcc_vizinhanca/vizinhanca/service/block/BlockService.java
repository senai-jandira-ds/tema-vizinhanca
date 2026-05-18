/***************************************************
 * Objetivo: Serviço responsável pela regra de negócio
 * dos blocos, gerenciando operações de consulta,
 * inserção, atualização e remoção no banco de dados
 * Data: 15/05/2026
 * Autor: Leonardo Scotti
 * Versão: 1.0.05.26
 * *************************************************/

package com.tcc_vizinhanca.vizinhanca.service.block;

import com.tcc_vizinhanca.vizinhanca.entity.condominium.Block;
import com.tcc_vizinhanca.vizinhanca.entity.condominium.Condominium;
import com.tcc_vizinhanca.vizinhanca.repository.block.BlockRepository;
import com.tcc_vizinhanca.vizinhanca.repository.condominium.CondominiumRepository;
import lombok.NonNull;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import java.util.List;

@Service
public class BlockService {

    @Autowired
    private BlockRepository blockRepository;

    @Autowired
    private CondominiumRepository condominiumRepository;

    // SELECT ALL
    public List<Block> getSelectAllBlocks() {
        return blockRepository.findAll();
    }

    // SELECT BY ID
    public Block getSelectBlockById(Long id) {
        return blockRepository.findById(id)
                .orElseThrow(() -> new ResponseStatusException(
                        HttpStatus.NOT_FOUND, "Bloco não encontrado no banco de dados!"));
    }

    // INSERT BLOCK
    public Block setInsertBlock(@NonNull Block block, String email) {
        Condominium condominium = condominiumRepository.findByEmail(email)
                .orElseThrow(() -> new ResponseStatusException(
                        HttpStatus.NOT_FOUND, "Condomínio não encontrado!"));

        block.setId(null);
        block.setCondominium(condominium);

        return blockRepository.save(block);
    }

    // UPDATE BLOCK
    public Block setUpdateBlock(@NonNull Block block, Long idBlock) {
        Block existingBlock = getSelectBlockById(idBlock);

        BeanUtils.copyProperties(block, existingBlock, "id", "condominium");

        return blockRepository.save(existingBlock);
    }

    // DELETE BLOCK
    public void setDeleteBlock(Long idBlock) {
        if (!blockRepository.existsById(idBlock)) {
            throw new ResponseStatusException(
                    HttpStatus.NOT_FOUND, "Bloco não encontrado no banco de dados!");
        }

        blockRepository.deleteById(idBlock);
    }
}