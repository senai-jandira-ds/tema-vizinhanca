/***************************************************
 * Objetivo: Serviço responsável pela regra de negócio
 * das publicações, gerenciando operações de consulta,
 * inserção, atualização e remoção no banco de dados
 * Data: 15/05/2026
 * Autor: Leonardo Scotti
 * Versão: 1.0.04.26
 * *************************************************/

package com.tcc_vizinhanca.vizinhanca.service.publication;

import com.tcc_vizinhanca.vizinhanca.entity.publication.Publication;
import com.tcc_vizinhanca.vizinhanca.entity.resident.Resident;
import com.tcc_vizinhanca.vizinhanca.repository.publication.PublicationRepository;
import com.tcc_vizinhanca.vizinhanca.repository.resident.ResidentRepository;
import com.tcc_vizinhanca.vizinhanca.service.resident.ResidentService;
import lombok.NonNull;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import java.util.List;

@Service
public class PublicationService {

    @Autowired
    private PublicationRepository publicationRepository;

    @Autowired
    private ResidentRepository residentRepository;

    // SELECT ALL
    public List<Publication> getSelectAllPublications(){
        return publicationRepository.findAll();
    }

    // SELECT BY ID
    public Publication getSelectPublicationById(Long id){
        return publicationRepository.findById(id)
                .orElseThrow(() -> new ResponseStatusException(
                        HttpStatus.NOT_FOUND,
                        "Publicação não encontrada no banco de dados!"));
    }

    public Publication setInsertPublication(@NonNull Publication publication, String email) {
        Resident resident = residentRepository.findByEmail(email)
                .orElseThrow(() -> new ResponseStatusException(
                        HttpStatus.NOT_FOUND, "Morador não encontrado!"));

        publication.setId(null);
        publication.setResident(resident);
        publication.setCondominium(resident.getCondominium());

        return publicationRepository.save(publication);
    }

    // UPDATE PUBLICATION
    public Publication setUpdatePublication(@NonNull Publication publication, Long idPublication){
        Publication existingPublication = getSelectPublicationById(idPublication);

        BeanUtils.copyProperties(publication, existingPublication, "id", "creationDate");
        existingPublication.setId(idPublication);

        return publicationRepository.save(existingPublication);
    }

    // DELETE PUBLICATION
    public void setDeletePublication(Long idPublication){
        if (!publicationRepository.existsById(idPublication)){
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Publicação não encontrada no Banco de Dados!");
        }

        publicationRepository.deleteById(idPublication);
    }
}
