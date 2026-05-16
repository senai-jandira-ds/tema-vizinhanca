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
import com.tcc_vizinhanca.vizinhanca.dto.response.service.ServiceDetailResponse;
import com.tcc_vizinhanca.vizinhanca.dto.response.service.ServiceResponse;
import com.tcc_vizinhanca.vizinhanca.entity.service.Service;
import com.tcc_vizinhanca.vizinhanca.security.jwt.JwtService;
import com.tcc_vizinhanca.vizinhanca.service.service.ServiceService;
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
@RequestMapping("/api/v1/service")
@Tag(name = "Service", description = "Endpoints para gerenciamento dos serviços.")
public class ServiceController {

    @Autowired
    private ServiceService serviceService;

    @Autowired
    private JwtService jwtService;

    // GET ALL BY CONDOMINIUM
    @GetMapping
    public ResponseEntity<ApiResponse<ServiceResponse>> listAllServices(HttpServletRequest request) {
        String token = request.getHeader("Authorization").substring(7);
        Long condominiumId = jwtService.extrairIdCondominio(token);

        List<Service> services = serviceService.getSelectAllServicesByCondominiumId(condominiumId);

        return ResponseEntity.ok(ResponseUtil.success(
                new ServiceResponse(services), "Serviços retornados com sucesso!"));
    }

    // GET BY STATUS
    @GetMapping("/status/{status}")
    public ResponseEntity<ApiResponse<ServiceResponse>> listServicesByStatus(
            @PathVariable String status, HttpServletRequest request) {
        String token = request.getHeader("Authorization").substring(7);
        Long condominiumId = jwtService.extrairIdCondominio(token);

        List<Service> services = serviceService.getSelectServicesByStatus(condominiumId, status);

        return ResponseEntity.ok(ResponseUtil.success(
                new ServiceResponse(services), "Serviços filtrados por status retornados com sucesso!"));
    }

    // GET BY CATEGORY
    @GetMapping("/category/{categoryId}")
    public ResponseEntity<ApiResponse<ServiceResponse>> listServicesByCategory(
            @PathVariable Long categoryId, HttpServletRequest request) {
        String token = request.getHeader("Authorization").substring(7);
        Long condominiumId = jwtService.extrairIdCondominio(token);

        List<Service> services = serviceService.getSelectServicesByCategory(condominiumId, categoryId);

        return ResponseEntity.ok(ResponseUtil.success(
                new ServiceResponse(services), "Serviços filtrados por categoria retornados com sucesso!"));
    }

    // GET BY RESIDENT
    @GetMapping("/resident/{residentId}")
    public ResponseEntity<ApiResponse<ServiceResponse>> listServicesByResident(
            @PathVariable Long residentId) {

        List<Service> services = serviceService.getSelectServicesByResidentId(residentId);

        return ResponseEntity.ok(ResponseUtil.success(
                new ServiceResponse(services), "Serviços do morador retornados com sucesso!"));
    }

    // GET BY ID
    @GetMapping("/{id}")
    public ResponseEntity<ApiResponse<ServiceDetailResponse>> searchServiceById(
            @PathVariable("id") Long id) {

        Service service = serviceService.getSelectServiceById(id);

        return ResponseEntity.ok(ResponseUtil.success(
                new ServiceDetailResponse(service), "Serviço encontrado com sucesso!"));
    }

    // POST
    @PostMapping
    public ResponseEntity<ApiResponse<ServiceDetailResponse>> insertService(
            @Valid @RequestBody ServiceCreateRequest request,
            HttpServletRequest httpRequest) {

        String token = httpRequest.getHeader("Authorization").substring(7);
        Long residentId = jwtService.extrairIdCondominio(token); // trocar para extrairIdMorador
        Long condominiumId = jwtService.extrairIdCondominio(token);

        Service service = new Service();
        service.setPhoto(request.getPhoto());
        service.setTitle(request.getTitle());
        service.setEstimatedTime(request.getEstimatedTime());
        service.setUrgency(request.getUrgency());
        service.setDescription(request.getDescription());
        service.setStatus(request.getStatus());

        Service saved = serviceService.setInsertService(service, residentId, condominiumId, request.getCategoryId());

        return ResponseEntity.status(HttpStatus.CREATED)
                .body(ResponseUtil.success(new ServiceDetailResponse(saved), "Serviço criado com sucesso!"));
    }

    // PUT
    @PutMapping("/{id}")
    public ResponseEntity<ApiResponse<ServiceDetailResponse>> updateService(
            @PathVariable Long id,
            @RequestBody ServiceUpdateRequest request) {

        Service service = new Service();
        service.setPhoto(request.getPhoto());
        service.setTitle(request.getTitle());
        service.setEstimatedTime(request.getEstimatedTime());
        service.setUrgency(request.getUrgency());
        service.setDescription(request.getDescription());
        service.setStatus(request.getStatus());

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