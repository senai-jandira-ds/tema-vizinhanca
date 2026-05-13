package com.tcc_vizinhanca.vizinhanca.service.publication;

import com.tcc_vizinhanca.vizinhanca.entity.publication.Publication;
import com.tcc_vizinhanca.vizinhanca.repository.publication.PublicationRepository;
import lombok.NonNull;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import java.util.List;

@Service
public class PublicationService {

    @Autowired
    private PublicationRepository publicationRepository;

    // SELECT ALL
    public List<Publication> getSelectAllPublications(){
        return publicationRepository.findAll();
    }

    // SELECT BY ID
    public Publication getSelectPublicationById(Long id){
        Publication publication = publicationRepository.findById(id)
                .orElseThrow(() -> new ResponseStatusException(
                        HttpStatus.NOT_FOUND,
                        "Publicação não encontrada no banco de dados!"));

        return publication;
    }

    // INSERT PUBLICATION
    public Publication setInsertPublication(@NonNull Publication publication){
        publication.setId(null);

        return publicationRepository.save(publication);


    }
}
