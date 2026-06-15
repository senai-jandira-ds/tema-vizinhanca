package com.tcc_vizinhanca.vizinhanca.repository.block;

import com.tcc_vizinhanca.vizinhanca.entity.condominium.Block;
import com.tcc_vizinhanca.vizinhanca.entity.condominium.Condominium;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface BlockRepository extends JpaRepository<Block, Long> {

    List<Block> findByCondominiumId(Long idCondominium);
}
