package com.tcc_vizinhanca.vizinhanca.service.publication;

import com.tcc_vizinhanca.vizinhanca.entity.publication.Publication;
import com.tcc_vizinhanca.vizinhanca.repository.publication.PublicationRepository;
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

    // INSERT PUBLICATION
    public Publication setInsertPublication(@NonNull Publication publication){
        publication.setId(null);

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
