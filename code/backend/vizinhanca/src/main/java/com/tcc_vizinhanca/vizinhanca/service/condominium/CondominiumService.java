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
import com.tcc_vizinhanca.vizinhanca.service.storage.BlobStorageService;
import lombok.NonNull;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.cache.annotation.Caching;
import org.springframework.http.HttpStatus;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.server.ResponseStatusException;

import java.util.List;

@Service
public class CondominiumService {

    @Autowired
    private CondominiumRepository condominiumRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @Autowired
    private BlobStorageService blobStorageService;

    // SELECT ALL
    public List<Condominium> getSelectAllCondominiums() {
        return condominiumRepository.findAll();
    }

    // SELECT BY ID — carrega address + blocks (leve, sem coleções grandes)
    @Cacheable(value = "condominium", key = "#id")
    public Condominium getSelectCondominiumById(Long id) {
        return condominiumRepository.findByIdWithDetails(id)
                .orElseThrow(() -> new ResponseStatusException(
                        HttpStatus.NOT_FOUND, "Condomínio não encontrado no Banco de Dados!"));
    }

    // SELECT BY EMAIL — busca leve usada no JwtFilter e auth, só carrega address
    @Cacheable(value = "residentByEmail", key = "#email")
    public Condominium getSelectCondominiumByEmail(String email) {
        return condominiumRepository.findByEmail(email)
                .orElseThrow(() -> new ResponseStatusException(
                        HttpStatus.NOT_FOUND, "Condomínio não encontrado!"));
    }

    /**
     * Busca detalhada usada no login e no /me.
     * Executa 4 queries separadas ao invés de um único JOIN FETCH com produto cartesiano.
     * O Hibernate faz merge das coleções no mesmo objeto gerenciado dentro da sessão.
     */
    @Transactional(readOnly = true)
    public Condominium getDetailedCondominiumByEmail(String email) {
        Condominium base = condominiumRepository.findWithAddressAndBlocksByEmail(email)
                .orElseThrow(() -> new ResponseStatusException(
                        HttpStatus.NOT_FOUND, "Condomínio não encontrado."));


        condominiumRepository.findWithResidentsByEmail(email);
        condominiumRepository.findWithServicesByEmail(email);
        condominiumRepository.findWithReportsByEmail(email);

        return base;
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

        condominium.setPassword(passwordEncoder.encode(condominium.getPassword()));

        return condominiumRepository.save(condominium);
    }

    // UPDATE CONDOMINIUM
    @Caching(evict = {
            @CacheEvict(value = "condominium", key = "#idCondominium"),
            @CacheEvict(value = "residentByEmail", allEntries = true)
    })
    public Condominium setUpdateCondominium(
            @NonNull Condominium condominium,
            MultipartFile photo,
            Long idCondominium) {

        Condominium existingCondominium = getSelectCondominiumById(idCondominium);

        if (photo != null && !photo.isEmpty()) {
            if (existingCondominium.getPhoto() != null) {
                blobStorageService.deleteFile(existingCondominium.getPhoto());
            }
            String photoUrl = blobStorageService.uploadFile(photo, "condominiums");
            existingCondominium.setPhoto(photoUrl);
        }

        BeanUtils.copyProperties(
                condominium,
                existingCondominium,
                "id", "password", "creationDate", "address", "residents", "services", "blocks");

        return condominiumRepository.save(existingCondominium);
    }

    // DELETE CONDOMINIUM
    @Caching(evict = {
            @CacheEvict(value = "condominium", key = "#idCondominium"),
            @CacheEvict(value = "residentByEmail", allEntries = true)
    })
    public void setDeleteCondominiumById(Long idCondominium) {
        if (!condominiumRepository.existsById(idCondominium)) {
            throw new ResponseStatusException(
                    HttpStatus.NOT_FOUND, "Condomínio não encontrado no Banco de Dados!");
        }

        condominiumRepository.deleteById(idCondominium);
    }
}
