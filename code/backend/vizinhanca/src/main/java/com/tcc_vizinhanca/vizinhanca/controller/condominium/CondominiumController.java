package com.tcc_vizinhanca.vizinhanca.controller.condominium;

import com.tcc_vizinhanca.vizinhanca.dto.request.condominium.CondominiumCreateRequest;
import com.tcc_vizinhanca.vizinhanca.dto.request.condominium.CondominiumUpdateRequest;
import com.tcc_vizinhanca.vizinhanca.dto.response.ApiResponse;
import com.tcc_vizinhanca.vizinhanca.dto.response.PageResponse;
import com.tcc_vizinhanca.vizinhanca.dto.response.condominium.ActivityViewResponse;
import com.tcc_vizinhanca.vizinhanca.dto.response.condominium.CondominiumDetailResponse;
import com.tcc_vizinhanca.vizinhanca.dto.response.condominium.CondominiumResponse;
import com.tcc_vizinhanca.vizinhanca.dto.response.resident.ResidentSummaryResponse;
import com.tcc_vizinhanca.vizinhanca.dto.response.service.ServiceResponse;
import com.tcc_vizinhanca.vizinhanca.dto.response.service.ServiceSummaryResponse;
import com.tcc_vizinhanca.vizinhanca.entity.condominium.ActivityView;
import com.tcc_vizinhanca.vizinhanca.entity.condominium.Condominium;
import com.tcc_vizinhanca.vizinhanca.entity.resident.Resident;
import com.tcc_vizinhanca.vizinhanca.entity.service.Service;
import com.tcc_vizinhanca.vizinhanca.mapper.condominium.CondominiumMapper;
import com.tcc_vizinhanca.vizinhanca.security.jwt.AuthenticatedUser;
import com.tcc_vizinhanca.vizinhanca.security.jwt.JwtService;
import com.tcc_vizinhanca.vizinhanca.service.condominium.ActivityViewService;
import com.tcc_vizinhanca.vizinhanca.service.condominium.CondominiumService;
import com.tcc_vizinhanca.vizinhanca.service.resident.ResidentService;
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
@RequestMapping("/api/v1/condominium")
@Tag(name = "Condominium", description = "Endpoints para gerenciamento dos condomínios.")
public class CondominiumController {

    @Autowired
    private CondominiumService condominiumService;

    @Autowired
    private ResidentService residentService;

    @Autowired
    private ActivityViewService activityViewService;

    @Autowired
    private ServiceService serviceService;

    // GET ALL
    @GetMapping
    public ResponseEntity<ApiResponse<CondominiumResponse>> listAllCondos() {
        List<Condominium> condominiums = condominiumService.getSelectAllCondominiums();
        CondominiumResponse response = new CondominiumResponse(condominiums);
        return ResponseEntity.ok(ResponseUtil.success(response, "Lista de condomínios retornada com sucesso!"));
    }

    // GET RESIDENTS
    @GetMapping("/resident/me")
    public ResponseEntity<ApiResponse<PageResponse<ResidentSummaryResponse>>> listAllResidentsByCondominium(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "20") int size,
            HttpServletRequest request) {

        AuthenticatedUser user =
                (AuthenticatedUser) SecurityContextHolder.getContext().getAuthentication().getPrincipal();

        Pageable pageable = PageRequest.of(page, size, Sort.by("name").ascending());
        Page<Resident> residents = residentService.getSelectResidentsByCondominiumId(user.idCondominium(), pageable);

        PageResponse<ResidentSummaryResponse> response =
                new PageResponse<>(residents, ResidentSummaryResponse::new);

        return ResponseEntity.ok(ResponseUtil.success(response, "Moradores encontrados com sucesso!"));
    }

    // GET ACTIVITIES
    @GetMapping("/activity/me")
    public ResponseEntity<ApiResponse<ActivityViewResponse>> listAllActivitiesByCondominium(
            HttpServletRequest request) {

        AuthenticatedUser user =
                (AuthenticatedUser) SecurityContextHolder.getContext().getAuthentication().getPrincipal();

        List<ActivityView> activities = activityViewService
                .getSelectActivitiesViewByCondominiumId(user.idCondominium());

        ActivityViewResponse response = new ActivityViewResponse(activities);
        return ResponseEntity.ok(ResponseUtil.success(response, "Atividades encontradas com sucesso!"));
    }

    // GET SERVICES
    @GetMapping("/service/me")
    public ResponseEntity<ApiResponse<PageResponse<ServiceSummaryResponse>>> listAllServicesByCondominium(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "20") int  size,
            HttpServletRequest request) {
        AuthenticatedUser user =
                (AuthenticatedUser) SecurityContextHolder.getContext().getAuthentication().getPrincipal();

        Pageable pageable = PageRequest.of(page, size, Sort.by("title").ascending());
        Page<Service> services = serviceService.getSelectAllServicesByCondominiumId(user.idCondominium(), pageable);

        PageResponse<ServiceSummaryResponse> response = new PageResponse<>(services, ServiceSummaryResponse::new);

        return ResponseEntity.ok(ResponseUtil.success(response, "Serviços encontrados com sucesso!"));

    }

    // GET BY ID
    @GetMapping("/{id}")
    public ResponseEntity<ApiResponse<CondominiumDetailResponse>> searchCondominiumById(
            @PathVariable("id") Long idCondominium) {

        Condominium condominium = condominiumService.getSelectCondominiumById(idCondominium);
        List<ActivityView> activities = activityViewService
                .getSelectActivitiesViewByCondominiumId(idCondominium);

        CondominiumDetailResponse response = new CondominiumDetailResponse(condominium, activities);
        return ResponseEntity.ok(ResponseUtil.success(response, "Condomínio encontrado com sucesso!"));
    }

    // POST
    @PostMapping
    public ResponseEntity<ApiResponse<CondominiumDetailResponse>> insertCondominium(
            @Valid @RequestBody CondominiumCreateRequest condominiumCreateRequest) {

        Condominium condominium = CondominiumMapper.toEntity(condominiumCreateRequest);
        Condominium newCondominium = condominiumService.setInsertCondominium(condominium);
        CondominiumDetailResponse response = new CondominiumDetailResponse(newCondominium);

        return ResponseEntity.status(HttpStatus.CREATED)
                .body(ResponseUtil.success(response, "Condomínio criado com sucesso!"));
    }

    // PUT
    @PutMapping("/{id}")
    public ResponseEntity<ApiResponse<CondominiumDetailResponse>> updateCondominium(
            @PathVariable Long id,
            @Valid @ModelAttribute CondominiumUpdateRequest condominiumUpdateRequest) {

        Condominium condominium = CondominiumMapper.updateEntity(condominiumUpdateRequest, new Condominium());
        Condominium updatedCondominium = condominiumService.setUpdateCondominium(
                condominium, condominiumUpdateRequest.getFoto(), id);

        CondominiumDetailResponse response = new CondominiumDetailResponse(updatedCondominium);
        return ResponseEntity.ok(ResponseUtil.success(response, "Condomínio atualizado com sucesso!"));
    }

    // DELETE
    @DeleteMapping("/{id}")
    public ResponseEntity<ApiResponse<Void>> deleteCondominium(@PathVariable Long id) {
        condominiumService.setDeleteCondominiumById(id);
        return ResponseEntity.ok(ResponseUtil.success(null, "Condomínio deletado com sucesso!"));
    }
}
