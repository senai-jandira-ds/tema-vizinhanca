/***************************************************
 * Objetivo: Controller responsável pelos endpoints de gerenciamento de serviços
 * Data: 15/05/2026
 * Autor: Leonardo Scotti
 * Versão: 1.0.05.26
 * *************************************************/

package com.tcc_vizinhanca.vizinhanca.controller.service;

import com.tcc_vizinhanca.vizinhanca.dto.request.service.ServiceCreateRequest;
import com.tcc_vizinhanca.vizinhanca.dto.request.service.ServiceUpdateRequest;
import com.tcc_vizinhanca.vizinhanca.dto.response.ApiResponse;
import com.tcc_vizinhanca.vizinhanca.dto.response.PageResponse;
import com.tcc_vizinhanca.vizinhanca.dto.response.service.ServiceDetailResponse;
import com.tcc_vizinhanca.vizinhanca.dto.response.service.ServiceResponse;
import com.tcc_vizinhanca.vizinhanca.entity.service.Service;
import com.tcc_vizinhanca.vizinhanca.security.jwt.AuthenticatedUser;
import com.tcc_vizinhanca.vizinhanca.security.jwt.JwtService;
import com.tcc_vizinhanca.vizinhanca.service.service.ServiceService;
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
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/v1/service")
@Tag(name = "Service", description = "Endpoints para gerenciamento dos serviços.")
public class ServiceController {

    @Autowired
    private ServiceService serviceService;

    // GET ALL BY CONDOMINIUM
    @GetMapping
    public ResponseEntity<ApiResponse<PageResponse<ServiceDetailResponse>>> listAllServices(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "20") int size,
            HttpServletRequest request) {

        AuthenticatedUser user =
                (AuthenticatedUser) SecurityContextHolder.getContext().getAuthentication().getPrincipal();

        Pageable pageable = PageRequest.of(page, size, Sort.by("id").descending());
        Page<Service> services = serviceService.getSelectAllServicesByCondominiumId(user.idCondominium(), pageable);

        return ResponseEntity.ok(ResponseUtil.success(
                new PageResponse<>(services, ServiceDetailResponse::new),
                "Serviços retornados com sucesso!"));
    }

    // GET BY STATUS
    @GetMapping("/status/{status}")
    public ResponseEntity<ApiResponse<PageResponse<ServiceDetailResponse>>> listServicesByStatus(
            @PathVariable String status,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "20") int size,
            HttpServletRequest request) {

        AuthenticatedUser user =
                (AuthenticatedUser) SecurityContextHolder.getContext().getAuthentication().getPrincipal();

        Pageable pageable = PageRequest.of(page, size, Sort.by("id").descending());
        Page<Service> services = serviceService.getSelectServicesByStatus(user.idCondominium(), status, pageable);

        return ResponseEntity.ok(ResponseUtil.success(
                new PageResponse<>(services, ServiceDetailResponse::new),
                "Serviços filtrados por status retornados com sucesso!"));
    }

    // GET BY CATEGORY
    @GetMapping("/category/{categoryId}")
    public ResponseEntity<ApiResponse<PageResponse<ServiceDetailResponse>>> listServicesByCategory(
            @PathVariable Long categoryId,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "20") int size,
            HttpServletRequest request) {

        AuthenticatedUser user =
                (AuthenticatedUser) SecurityContextHolder.getContext().getAuthentication().getPrincipal();

        Pageable pageable = PageRequest.of(page, size, Sort.by("id").descending());
        Page<Service> services = serviceService.getSelectServicesByCategory(user.idCondominium(), categoryId, pageable);

        return ResponseEntity.ok(ResponseUtil.success(
                new PageResponse<>(services, ServiceDetailResponse::new),
                "Serviços filtrados por categoria retornados com sucesso!"));
    }

    // GET BY RESIDENT
    @GetMapping("/resident/{residentId}")
    public ResponseEntity<ApiResponse<PageResponse<ServiceDetailResponse>>> listServicesByResident(
            @PathVariable Long residentId,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "20") int size) {

        Pageable pageable = PageRequest.of(page, size, Sort.by("id").descending());
        Page<Service> services = serviceService.getSelectServicesByResidentId(residentId, pageable);

        return ResponseEntity.ok(ResponseUtil.success(
                new PageResponse<>(services, ServiceDetailResponse::new),
                "Serviços do morador retornados com sucesso!"));
    }

    // GET BY ID
    @GetMapping("/{id}")
    public ResponseEntity<ApiResponse<ServiceDetailResponse>> searchServiceById(@PathVariable Long id) {
        Service service = serviceService.getSelectServiceById(id);

        return ResponseEntity.ok(ResponseUtil.success(
                new ServiceDetailResponse(service), "Serviço encontrado com sucesso!"));
    }

    // POST
    @PostMapping
    public ResponseEntity<ApiResponse<ServiceDetailResponse>> insertService(
            @Valid @RequestBody ServiceCreateRequest request,
            HttpServletRequest httpRequest) {

        AuthenticatedUser user =
                (AuthenticatedUser) SecurityContextHolder.getContext().getAuthentication().getPrincipal();

        Service service = new Service(request);

        Service saved = serviceService.setInsertService(
                service, user.idResident(), user.idCondominium(), request.getCategoryId());

        return ResponseEntity.status(HttpStatus.CREATED)
                .body(ResponseUtil.success(new ServiceDetailResponse(saved), "Serviço criado com sucesso!"));
    }

    // PUT
    @PutMapping("/{id}")
    public ResponseEntity<ApiResponse<ServiceDetailResponse>> updateService(
            @PathVariable Long id,
            @RequestBody ServiceUpdateRequest request) {

        Service service = new Service(request);

        Service updated = serviceService.setUpdateService(id, service, request.getCategoryId());

        return ResponseEntity.ok(ResponseUtil.success(
                new ServiceDetailResponse(updated), "Serviço atualizado com sucesso!"));
    }

    // DELETE
    @DeleteMapping("/{id}")
    public ResponseEntity<ApiResponse<Void>> deleteService(@PathVariable Long id) {
        serviceService.setDeleteServiceById(id);
        
        return ResponseEntity.ok(ResponseUtil.success(null, "Serviço deletado com sucesso!"));
    }
}