/***************************************************
 * Objetivo: Controlador responsável por expor os endpoints
 * da entidade Object, gerenciando as requisições HTTP
 * de listagem, busca, criação, atualização e remoção
 * Data: 26/05/2026
 * Autor: Leonardo Scotti
 * Versão: 1.0.05.26
 * *************************************************/

package com.tcc_vizinhanca.vizinhanca.controller.object;

import com.tcc_vizinhanca.vizinhanca.dto.request.object.ObjectCreateRequest;
import com.tcc_vizinhanca.vizinhanca.dto.request.object.ObjectUpdateRequest;
import com.tcc_vizinhanca.vizinhanca.dto.response.ApiResponse;
import com.tcc_vizinhanca.vizinhanca.dto.response.PageResponse;
import com.tcc_vizinhanca.vizinhanca.dto.response.object.ObjectDetailResponse;
import com.tcc_vizinhanca.vizinhanca.entity.object.Object;
import com.tcc_vizinhanca.vizinhanca.mapper.object.ObjectMapper;
import com.tcc_vizinhanca.vizinhanca.security.jwt.AuthenticatedUser;
import com.tcc_vizinhanca.vizinhanca.service.object.ObjectService;
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

@RestController
@RequestMapping("/api/v1/object")
@Tag(name = "Object", description = "Endpoints para gerenciamento dos objetos.")
public class ObjectController {

    @Autowired
    private ObjectService objectService;

    // GET ALL BY CONDOMINIUM — ex: GET /api/v1/object?page=0&size=20
    @GetMapping
    public ResponseEntity<ApiResponse<PageResponse<ObjectDetailResponse>>> listAllObjects(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "20") int size,
            HttpServletRequest request) {

        AuthenticatedUser user =
                (AuthenticatedUser) SecurityContextHolder.getContext().getAuthentication().getPrincipal();

        Pageable pageable = PageRequest.of(page, size, Sort.by("id").descending());
        Page<Object> objects = objectService.getSelectAllObjectsByCondominiumId(user.idCondominium(), pageable);

        return ResponseEntity.ok(ResponseUtil.success(
                new PageResponse<>(objects, ObjectDetailResponse::new),
                "Objetos retornados com sucesso!"));
    }

    // GET BY STATUS
    @GetMapping("/status/{status}")
    public ResponseEntity<ApiResponse<PageResponse<ObjectDetailResponse>>> listObjectsByStatus(
            @PathVariable String status,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "20") int size,
            HttpServletRequest request) {

        AuthenticatedUser user =
                (AuthenticatedUser) SecurityContextHolder.getContext().getAuthentication().getPrincipal();

        Pageable pageable = PageRequest.of(page, size, Sort.by("id").descending());
        Page<Object> objects = objectService.getSelectObjectsByStatus(user.idCondominium(), status, pageable);

        return ResponseEntity.ok(ResponseUtil.success(
                new PageResponse<>(objects, ObjectDetailResponse::new),
                "Objetos filtrados por status retornados com sucesso!"));
    }

    // GET BY CATEGORY
    @GetMapping("/category/{categoryId}")
    public ResponseEntity<ApiResponse<PageResponse<ObjectDetailResponse>>> listObjectsByCategory(
            @PathVariable Long categoryId,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "20") int size,
            HttpServletRequest request) {

        AuthenticatedUser user =
                (AuthenticatedUser) SecurityContextHolder.getContext().getAuthentication().getPrincipal();

        Pageable pageable = PageRequest.of(page, size, Sort.by("id").descending());
        Page<Object> objects = objectService.getSelectObjectsByCategory(user.idCondominium(), categoryId, pageable);

        return ResponseEntity.ok(ResponseUtil.success(
                new PageResponse<>(objects, ObjectDetailResponse::new),
                "Objetos filtrados por categoria retornados com sucesso!"));
    }

    // GET BY RESIDENT
    @GetMapping("/resident/{residentId}")
    public ResponseEntity<ApiResponse<PageResponse<ObjectDetailResponse>>> listObjectsByResident(
            @PathVariable Long residentId,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "20") int size) {

        Pageable pageable = PageRequest.of(page, size, Sort.by("id").descending());
        Page<Object> objects = objectService.getSelectObjectsByResidentId(residentId, pageable);

        return ResponseEntity.ok(ResponseUtil.success(
                new PageResponse<>(objects, ObjectDetailResponse::new),
                "Objetos do morador retornados com sucesso!"));
    }

    // GET BY ID
    @GetMapping("/{id}")
    public ResponseEntity<ApiResponse<ObjectDetailResponse>> searchObjectById(@PathVariable Long id) {
        Object object = objectService.getSelectObjectById(id);
        return ResponseEntity.ok(ResponseUtil.success(
                new ObjectDetailResponse(object), "Objeto encontrado com sucesso!"));
    }

    // POST
    @PostMapping(consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<ApiResponse<ObjectDetailResponse>> insertObject(
            @Valid @ModelAttribute ObjectCreateRequest objectRequest,
            HttpServletRequest httpRequest) {

        AuthenticatedUser user =
                (AuthenticatedUser) SecurityContextHolder.getContext().getAuthentication().getPrincipal();

        Object object = ObjectMapper.toEntity(objectRequest);
        Object saved = objectService.setInsertObject(
                object,
                objectRequest.getPhoto(),
                user.idResident(),
                user.idCondominium(),
                objectRequest.getCategoryId());

        return ResponseEntity.status(HttpStatus.CREATED)
                .body(ResponseUtil.success(new ObjectDetailResponse(saved), "Objeto criado com sucesso!"));
    }

    // PUT
    @PutMapping(value = "/{id}", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<ApiResponse<ObjectDetailResponse>> updateObject(
            @PathVariable Long id,
            @ModelAttribute ObjectUpdateRequest objectRequest) {

        Object object = ObjectMapper.updateEntity(objectRequest, new Object());
        Object updated = objectService.setUpdateObject(
                id,
                object,
                objectRequest.getPhoto(),
                objectRequest.getCategoryId());

        return ResponseEntity.ok(ResponseUtil.success(
                new ObjectDetailResponse(updated), "Objeto atualizado com sucesso!"));
    }

    // DELETE
    @DeleteMapping("/{id}")
    public ResponseEntity<ApiResponse<Void>> deleteObject(@PathVariable Long id) {
        objectService.setDeleteObjectById(id);
        return ResponseEntity.ok(ResponseUtil.success(null, "Objeto deletado com sucesso!"));
    }
}