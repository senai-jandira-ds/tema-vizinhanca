package com.tcc_vizinhanca.vizinhanca.controller.resident;

import com.tcc_vizinhanca.vizinhanca.dto.request.resident.ResidentCreateRequest;
import com.tcc_vizinhanca.vizinhanca.dto.request.resident.ResidentUpdateRequest;
import com.tcc_vizinhanca.vizinhanca.dto.response.ApiResponse;
import com.tcc_vizinhanca.vizinhanca.dto.response.PageResponse;
import com.tcc_vizinhanca.vizinhanca.dto.response.object.ObjectDetailResponse;
import com.tcc_vizinhanca.vizinhanca.dto.response.resident.ResidentDetailResponse;
import com.tcc_vizinhanca.vizinhanca.dto.response.service.ServiceDetailResponse;
import com.tcc_vizinhanca.vizinhanca.entity.condominium.Block;
import com.tcc_vizinhanca.vizinhanca.entity.condominium.Condominium;
import com.tcc_vizinhanca.vizinhanca.entity.resident.Resident;
import com.tcc_vizinhanca.vizinhanca.entity.service.Service;
import com.tcc_vizinhanca.vizinhanca.entity.object.Object;
import com.tcc_vizinhanca.vizinhanca.mapper.resident.ResidentMapper;
import com.tcc_vizinhanca.vizinhanca.security.jwt.AuthenticatedUser;
import com.tcc_vizinhanca.vizinhanca.service.block.BlockService;
import com.tcc_vizinhanca.vizinhanca.service.condominium.CondominiumService;
import com.tcc_vizinhanca.vizinhanca.service.object.ObjectService;
import com.tcc_vizinhanca.vizinhanca.service.resident.ResidentService;
import com.tcc_vizinhanca.vizinhanca.service.service.ServiceService;
import com.tcc_vizinhanca.vizinhanca.util.ResponseUtil;
import io.swagger.v3.oas.annotations.tags.Tag;
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
@RequestMapping("/api/v1/resident")
@Tag(name = "Resident", description = "Endpoints para gerenciamento dos moradores.")
public class ResidentController {

    @Autowired
    private ResidentService residentService;

    @Autowired
    private CondominiumService condominiumService;

    @Autowired
    private BlockService blockService;

    @Autowired
    private ServiceService serviceService;

    @Autowired
    private ObjectService objectService;

    // GET ALL — ex: GET /api/v1/resident?page=0&size=20
    @GetMapping
    public ResponseEntity<ApiResponse<PageResponse<ResidentDetailResponse>>> listAllResidents(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "20") int size
    ) {
        Pageable pageable = PageRequest.of(page, size, Sort.by("name").ascending());
        Page<Resident> residents = residentService.getSelectAllResidents(pageable);
        PageResponse<ResidentDetailResponse> response =
                new PageResponse<>(residents, ResidentDetailResponse::new);

        return ResponseEntity.ok(ResponseUtil.success(response, "Lista de Moradores retornada com sucesso!"));
    }

    // GET BY ID
    @GetMapping("/{id}")
    public ResponseEntity<ApiResponse<ResidentDetailResponse>> searchResidentById(
            @PathVariable("id") Long idResident) {

        Resident resident = residentService.getSelectResidentById(idResident);
        ResidentDetailResponse response = new ResidentDetailResponse(resident);

        return ResponseEntity.ok(ResponseUtil.success(response, "Morador encontrado com sucesso!"));
    }

    // GET SERVICES DO MORADOR AUTENTICADO
    @GetMapping("/service/me")
    public ResponseEntity<ApiResponse<PageResponse<ServiceDetailResponse>>> listMyServices(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "20") int size
    ) {
        AuthenticatedUser user =
                (AuthenticatedUser) SecurityContextHolder.getContext().getAuthentication().getPrincipal();

        Pageable pageable = PageRequest.of(page, size, Sort.by("id").descending());
        Page<Service> services = serviceService.getSelectServicesByResidentId(user.idResident(), pageable);

        return ResponseEntity.ok(ResponseUtil.success(
                new PageResponse<>(services, ServiceDetailResponse::new),
                "Serviços do morador retornados com sucesso!"));
    }

    // GET SERVICES DO MORADOR COM FILTROS
    // ex: GET /api/v1/resident/service/me/filter?statuses=PENDENTE&statuses=EM_ANDAMENTO&categoryIds=1
    @GetMapping("/service/me/filter")
    public ResponseEntity<ApiResponse<PageResponse<ServiceDetailResponse>>> listMyServicesByFilters(
            @RequestParam(required = false) List<String> statuses,
            @RequestParam(required = false) List<Long> categoryIds,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "20") int size
    ) {
        AuthenticatedUser user =
                (AuthenticatedUser) SecurityContextHolder.getContext().getAuthentication().getPrincipal();

        Pageable pageable = PageRequest.of(page, size, Sort.by("id").descending());
        Page<Service> services = serviceService
                .getSelectResidentServicesByFilters(user.idResident(), statuses, categoryIds, pageable);

        return ResponseEntity.ok(ResponseUtil.success(
                new PageResponse<>(services, ServiceDetailResponse::new),
                "Serviços do morador filtrados retornados com sucesso!"));
    }

    // GET OBJETOS DO MORADOR AUTENTICADO
    @GetMapping("/object/me")
    public ResponseEntity<ApiResponse<PageResponse<ObjectDetailResponse>>> listMyObjects(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "20") int size
    ) {
        AuthenticatedUser user =
                (AuthenticatedUser) SecurityContextHolder.getContext().getAuthentication().getPrincipal();

        Pageable pageable = PageRequest.of(page, size, Sort.by("id").descending());
        Page<Object> objects = objectService.getSelectObjectsByResidentId(user.idResident(), pageable);

        return ResponseEntity.ok(ResponseUtil.success(
                new PageResponse<>(objects, ObjectDetailResponse::new),
                "Objetos do morador retornados com sucesso!"));
    }

    // GET OBJETOS DO MORADOR COM FILTROS
    @GetMapping("/object/me/filter")
    public ResponseEntity<ApiResponse<PageResponse<ObjectDetailResponse>>> listMyObjectsByFilters(
            @RequestParam(required = false) List<String> statuses,
            @RequestParam(required = false) List<Long> categoryIds,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "20") int size
    ) {
        AuthenticatedUser user =
                (AuthenticatedUser) SecurityContextHolder.getContext().getAuthentication().getPrincipal();

        Pageable pageable = PageRequest.of(page, size, Sort.by("id").descending());
        Page<Object> objects = objectService
                .getSelectResidentObjectsByFilters(user.idResident(), statuses, categoryIds, pageable);

        return ResponseEntity.ok(ResponseUtil.success(
                new PageResponse<>(objects, ObjectDetailResponse::new),
                "Objetos do morador filtrados retornados com sucesso!"));
    }

    // POST
    @PostMapping
    public ResponseEntity<ApiResponse<ResidentDetailResponse>> insertResident(
            @Valid @RequestBody ResidentCreateRequest residentCreateRequest) {

        Condominium condominium = condominiumService
                .getSelectCondominiumById(residentCreateRequest.getIdCondominium());

        Block block = blockService.getSelectBlockById(residentCreateRequest.getIdBlock());

        Resident resident = ResidentMapper.toEntity(residentCreateRequest, condominium, block);
        Resident newResident = residentService.setInsertResident(resident);

        ResidentDetailResponse response = new ResidentDetailResponse(newResident);

        return ResponseEntity.status(HttpStatus.CREATED)
                .body(ResponseUtil.success(response, "Morador criado com sucesso!"));
    }

    // PUT
    @PutMapping("/{id}")
    public ResponseEntity<ApiResponse<ResidentDetailResponse>> updateResident(
            @PathVariable Long id,
            @Valid @ModelAttribute ResidentUpdateRequest residentUpdateRequest) {

        Resident updatedResident = residentService
                .setUpdateResident(residentUpdateRequest, residentUpdateRequest.getPhoto(), id);
        ResidentDetailResponse response = new ResidentDetailResponse(updatedResident);

        return ResponseEntity.ok(ResponseUtil.success(response, "Morador atualizado com sucesso!"));
    }

    // DELETE
    @DeleteMapping("/{id}")
    public ResponseEntity<ApiResponse<Void>> deleteResident(@PathVariable("id") Long idResident) {
        residentService.setDeleteResidentById(idResident);
        return ResponseEntity.ok(ResponseUtil.success(null, "Morador deletado com sucesso!"));
    }
}
