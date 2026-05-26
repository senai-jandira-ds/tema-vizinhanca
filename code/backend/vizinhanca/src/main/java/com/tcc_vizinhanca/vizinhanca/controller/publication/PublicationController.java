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
import com.tcc_vizinhanca.vizinhanca.dto.response.PageResponse;
import com.tcc_vizinhanca.vizinhanca.dto.response.publication.PublicationDetailResponse;
import com.tcc_vizinhanca.vizinhanca.dto.response.publication.PublicationResponse;
import com.tcc_vizinhanca.vizinhanca.entity.publication.Publication;
import com.tcc_vizinhanca.vizinhanca.mapper.publication.PublicationMapper;
import com.tcc_vizinhanca.vizinhanca.security.jwt.AuthenticatedUser;
import com.tcc_vizinhanca.vizinhanca.security.jwt.JwtService;
import com.tcc_vizinhanca.vizinhanca.service.publication.PublicationService;
import com.tcc_vizinhanca.vizinhanca.util.ResponseUtil;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/v1/publication")
@Tag(name = "Publication", description = "Endpoints para gerenciamento das publicações.")
public class PublicationController {

    @Autowired
    private PublicationService publicationService;

    // GET ALL — ex: GET /api/v1/publication?page=0&size=20
    @GetMapping
    public ResponseEntity<ApiResponse<PageResponse<PublicationDetailResponse>>> listAllPublications(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "20") int size
    ) {
        Pageable pageable = PageRequest.of(page, size, Sort.by("creationDate").descending());
        Page<Publication> publications = publicationService.getSelectAllPublications(pageable);
        PageResponse<PublicationDetailResponse> response =
                new PageResponse<>(publications, PublicationDetailResponse::new);

        return ResponseEntity.ok(ResponseUtil.success(response, "Lista de publicações retornada com sucesso!"));
    }

    // GET BY ID
    @GetMapping("/{id}")
    public ResponseEntity<ApiResponse<PublicationDetailResponse>> searchPublicationById(
            @PathVariable("id") Long idPublication) {

        Publication publication = publicationService.getSelectPublicationById(idPublication);
        PublicationDetailResponse response = new PublicationDetailResponse(publication);

        return ResponseEntity.ok(ResponseUtil.success(response, "Publicação encontrada com sucesso!"));
    }

    // POST
    @PostMapping
    public ResponseEntity<ApiResponse<PublicationDetailResponse>> insertPublication(
            @Valid @ModelAttribute PublicationRequest publicationRequest) {

        AuthenticatedUser user =
                (AuthenticatedUser) SecurityContextHolder.getContext().getAuthentication().getPrincipal();

        Publication publication = PublicationMapper.toEntity(publicationRequest);
        Publication newPublication = publicationService.setInsertPublication(
                publication, publicationRequest.getPhoto(), user.email());
        PublicationDetailResponse response = new PublicationDetailResponse(newPublication);

        return ResponseEntity.status(HttpStatus.CREATED)
                .body(ResponseUtil.success(response, "Publicação criada com sucesso!"));
    }

    // PUT
    @PutMapping(value = "/{id}", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<ApiResponse<PublicationDetailResponse>> updatePublication(
            @Valid @ModelAttribute PublicationRequest publicationRequest,
            @PathVariable Long id) {

        Publication publication = PublicationMapper.updateEntity(publicationRequest, new Publication());
        Publication updatedPublication = publicationService.setUpdatePublication(
                publication, publicationRequest.getPhoto(), id);
        PublicationDetailResponse response = new PublicationDetailResponse(updatedPublication);

        return ResponseEntity.ok(ResponseUtil.success(response, "Publicação atualizada com sucesso!"));
    }

    // DELETE
    @DeleteMapping("/{id}")
    public ResponseEntity<ApiResponse<Void>> deletePublication(@PathVariable Long id) {
        publicationService.setDeletePublication(id);
        return ResponseEntity.ok(ResponseUtil.success(null, "Publicação deletada com sucesso!"));
    }
}
