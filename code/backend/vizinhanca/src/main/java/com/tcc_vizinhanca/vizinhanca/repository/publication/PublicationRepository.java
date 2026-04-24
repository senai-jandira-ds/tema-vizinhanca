package com.tcc_vizinhanca.vizinhanca.repository.publication;

import com.tcc_vizinhanca.vizinhanca.entity.publication.Publication;
import org.springframework.data.jpa.repository.JpaRepository;

public interface PublicationRepository extends JpaRepository<Publication, Long> {
}
