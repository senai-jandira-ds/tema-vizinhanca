/***************************************************
 * Objetivo: Serviço responsável pelas regras de negócio relacionadas
 * à entidade Condominium, incluindo operações de CRUD e validações
 * de dados
 * Data: 24/04/2026
 * Autor: Leonardo Scotti
 * Versão: 1.0.04.26
 * *************************************************/

package com.tcc_vizinhanca.vizinhanca.service.condominium;

import com.tcc_vizinhanca.vizinhanca.entity.condominium.Condominium;
import com.tcc_vizinhanca.vizinhanca.repository.condominium.CondominiumRepository;
import lombok.NonNull;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.http.HttpStatus;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import java.util.List;

@Service
public class CondominiumService {

    @Autowired
    private CondominiumRepository condominiumRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;

    // SELECT ALL
    public List<Condominium> getSelectAllCondominiums() {
        return condominiumRepository.findAll();
    }

    // SELECT BY ID
    @Cacheable(value = "condominium", key = "#id")
    public Condominium getSelectCondominiumById(Long id) {
        return condominiumRepository.findByIdWithDetails(id)
                .orElseThrow(() -> new ResponseStatusException(
                        HttpStatus.NOT_FOUND, "Condomínio não encontrado no Banco de Dados!"));
    }

    // SELECT BY EMAIL
    public Condominium getSelectCondominiumByEmail(String email) {
        return condominiumRepository.findByEmail(email)
                .orElseThrow(() -> new ResponseStatusException(
                        HttpStatus.NOT_FOUND, "Condomínio não encontrado!"));
    }

    // INSERT CONDOMINIUM
    public Condominium setInsertCondominium(@NonNull Condominium condominium) {
        condominium.setId(null);

        if (condominiumRepository.existsByCnpj(condominium.getCnpj())) {
            throw new ResponseStatusException(HttpStatus.CONFLICT, "CNPJ já cadastrado!");
        }

        if (condominiumRepository.existsByEmail(condominium.getEmail())) {
            throw new ResponseStatusException(HttpStatus.CONFLICT, "E-mail já cadastrado!");
        }

        condominium.setPassword(
                passwordEncoder.encode(condominium.getPassword())
        );

        return condominiumRepository.save(condominium);
    }

    // UPDATE CONDOMINIUM
    @CacheEvict(value = "condominium", key = "#idCondominium")
    public Condominium setUpdateCondominium(@NonNull Condominium condominium, Long idCondominium) {
        Condominium existingCondominium = getSelectCondominiumById(idCondominium);

        BeanUtils.copyProperties(
                condominium,
                existingCondominium,
                "id",
                "password",
                "creationDate",
                "address",
                "residents",
                "services",
                "blocks");

        return condominiumRepository.save(existingCondominium);
    }

    // DELETE CONDOMINIUM
    @CacheEvict(value = "condominium", key = "#idCondominium")
    public void  setDeleteCondominiumById(Long idCondominium) {
        if (!condominiumRepository.existsById(idCondominium)) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Condomínio não encontrado no Bando de Dados!");
        }

        condominiumRepository.deleteById(idCondominium);
    }
}
