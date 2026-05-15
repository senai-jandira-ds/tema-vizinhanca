/***************************************************
 * Objetivo: Controlador responsável por expor os endpoints
 * da entidade Publication, gerenciando as requisições HTTP
 * de listagem, busca, criação, atualização e remoção
 * Data: 15/05/2026
 * Autor: Leonardo Scotti
 * Versão: 1.0.04.26
 * *************************************************/

package com.tcc_vizinhanca.vizinhanca.controller.publication;

import com.tcc_vizinhanca.vizinhanca.dto.request.publication.PublicationRequest;
import com.tcc_vizinhanca.vizinhanca.dto.response.ApiResponse;
import com.tcc_vizinhanca.vizinhanca.dto.response.publication.PublicationDetailResponse;
import com.tcc_vizinhanca.vizinhanca.dto.response.publication.PublicationResponse;
import com.tcc_vizinhanca.vizinhanca.entity.publication.Publication;
import com.tcc_vizinhanca.vizinhanca.mapper.publication.PublicationMapper;
import com.tcc_vizinhanca.vizinhanca.security.jwt.JwtService;
import com.tcc_vizinhanca.vizinhanca.service.publication.PublicationService;
import com.tcc_vizinhanca.vizinhanca.util.ResponseUtil;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/v1/publication")
@Tag(name = "Publicação", description = "Endpoints para gerenciamento das publicações.")
public class PublicationController {

    @Autowired
    private PublicationService publicationService;

    @Autowired
    private JwtService jwtService;

    // GET ALL
    @GetMapping
    public ResponseEntity<ApiResponse<PublicationResponse>> listAllPublications() {
        List<Publication> publications = publicationService.getSelectAllPublications();

        PublicationResponse response = new PublicationResponse(publications);

        return ResponseEntity.ok(ResponseUtil.success(response, "Lista de publicações retornada com sucesso!"));
    }

    // GET BY ID
    @GetMapping("/{id}")
    public ResponseEntity<ApiResponse<PublicationDetailResponse>> searchPublicationById(@PathVariable("id") Long idPublication) {
        Publication publication = publicationService.getSelectPublicationById(idPublication);

        PublicationDetailResponse response = new PublicationDetailResponse(publication);

        return ResponseEntity.ok(ResponseUtil.success(response, "Publicação encontrada com sucesso!"));
    }

    @PostMapping
    public ResponseEntity<ApiResponse<PublicationDetailResponse>> insertPublication(
            @Valid @RequestBody PublicationRequest publicationRequest,
            HttpServletRequest request) {

        String token = request.getHeader("Authorization").substring(7);
        String email = jwtService.extrairUsername(token);

        Publication publication = PublicationMapper.toEntity(publicationRequest);
        Publication newPublication = publicationService.setInsertPublication(publication, email);

        PublicationDetailResponse response = new PublicationDetailResponse(newPublication);

        return ResponseEntity.status(HttpStatus.CREATED)
                .body(ResponseUtil.success(response, "Publicação criada com sucesso!"));
    }

    // PUT
    @PutMapping("{id}")
    public ResponseEntity<ApiResponse<PublicationDetailResponse>> updatePublication(
            @Valid @RequestBody PublicationRequest publicationRequest, @PathVariable Long id
            ) {
        Publication publication = PublicationMapper.updateEntity(publicationRequest, new Publication());

        Publication updatedPublication = publicationService.setUpdatePublication(publication, id);

        PublicationDetailResponse response = new PublicationDetailResponse(updatedPublication);

        return ResponseEntity.ok(ResponseUtil.success(response, "Publicação atualizada com sucesso!"));
    }

    // DELETE
    @DeleteMapping("{id}")
    public ResponseEntity<ApiResponse<Void>> deletePublication(@PathVariable Long id) {
        publicationService.setDeletePublication(id);

        return ResponseEntity.ok(ResponseUtil.success(null, "Publicação deletada com sucesso!"));
    }

}
