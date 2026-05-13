package com.tcc_vizinhanca.vizinhanca.controller.condominium;

import com.tcc_vizinhanca.vizinhanca.dto.request.condominium.CondominiumCreateRequest;
import com.tcc_vizinhanca.vizinhanca.dto.request.condominium.CondominiumUpdateRequest;
import com.tcc_vizinhanca.vizinhanca.dto.response.ApiResponse;
import com.tcc_vizinhanca.vizinhanca.dto.response.condominium.ActivityViewResponse;
import com.tcc_vizinhanca.vizinhanca.dto.response.condominium.CondominiumDetailResponse;
import com.tcc_vizinhanca.vizinhanca.dto.response.condominium.CondominiumResponse;
import com.tcc_vizinhanca.vizinhanca.dto.response.resident.ResidentSummaryResponse;
import com.tcc_vizinhanca.vizinhanca.entity.condominium.ActivityView;
import com.tcc_vizinhanca.vizinhanca.entity.condominium.Condominium;
import com.tcc_vizinhanca.vizinhanca.entity.resident.Resident;
import com.tcc_vizinhanca.vizinhanca.mapper.CondominiumMapper;
import com.tcc_vizinhanca.vizinhanca.security.jwt.JwtService;
import com.tcc_vizinhanca.vizinhanca.service.condominium.ActivityViewService;
import com.tcc_vizinhanca.vizinhanca.service.condominium.CondominiumService;
import com.tcc_vizinhanca.vizinhanca.service.resident.ResidentService;
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
@RequestMapping("/api/v1/condominium")
@Tag(name = "Condominium", description = "Endpoints para gerenciamento dos condomínios.")
public class CondominiumController {

    @Autowired
    private CondominiumService condominiumService;

    @Autowired
    private JwtService jwtService;

    @Autowired
    private ResidentService residentService;

    @Autowired
    private ActivityViewService activityViewService;

    // GET ALL
    @GetMapping
    public ResponseEntity<ApiResponse<CondominiumResponse>> listAllCondos() {
        List<Condominium> condos = condominiumService.getSelectAllCondominiums();

        CondominiumResponse response = new CondominiumResponse(condos);

        return ResponseEntity.ok(ResponseUtil.success(response, "Lista de condomínios retornada com sucesso!"));
    }

    // GET RESIDENTS
    @GetMapping("/resident/me")
    public ResponseEntity<ApiResponse<List<ResidentSummaryResponse>>> listAllResidentsByCondominium(
            HttpServletRequest request
    ) {
        String token = request.getHeader("Authorization").substring(7);

        Long idCondominio = jwtService.extrairIdCondominio(token);

        List<Resident> residents = residentService.getSelectResidentsByCondominiumId(idCondominio);

        List<ResidentSummaryResponse> response = residents.stream()
                .map(ResidentSummaryResponse::new)
                .toList();

        return ResponseEntity.ok(ResponseUtil.success(response, "Moradores encontrados com sucesso!"));
    }

    // GET ACTIVITIES
    @GetMapping("/activity/me")
    public  ResponseEntity<ApiResponse<ActivityViewResponse>> listAllActivitiesByCondominium(
            HttpServletRequest request
    ) {
        String token = request.getHeader("Authorization").substring(7);
        System.out.println("Token: " + token);

        Long idCondominium = jwtService.extrairIdCondominio(token);
        System.out.println(idCondominium);

        List<ActivityView> activities = activityViewService.getSelectActivitiesViewByCondominiumId(idCondominium);

        ActivityViewResponse response = new ActivityViewResponse(activities);

        return ResponseEntity.ok(ResponseUtil.success(response, "Atividades encontradas com sucesso!"));
    }

    // GET BY ID
    @GetMapping("/{id}")
    public ResponseEntity<ApiResponse<CondominiumDetailResponse>> searchCondominiumById(@PathVariable("id") Long idCondominium) {

        Condominium condominium = condominiumService.getSelectCondominiumById(idCondominium);

        CondominiumDetailResponse response = new CondominiumDetailResponse(condominium);

        return ResponseEntity.ok(ResponseUtil.success(response, "Condomínio encontrado com sucesso!"));
    }

    // POST
    @PostMapping
    public ResponseEntity<ApiResponse<CondominiumDetailResponse>> insertCondominium(@Valid @RequestBody CondominiumCreateRequest condominiumCreateRequest) {

        Condominium condominium = CondominiumMapper.toEntity(condominiumCreateRequest);

        Condominium newCondominium = condominiumService.setInsertCondominium(condominium);

        CondominiumDetailResponse response = new CondominiumDetailResponse(newCondominium);

        return ResponseEntity.status(HttpStatus.CREATED)
                .body(ResponseUtil.success(response, "Condomínio criado com sucesso!"));
    }

    // PUT
    @PutMapping("/{id}")
    public ResponseEntity<ApiResponse<CondominiumDetailResponse>> updateCondominium(
            @PathVariable Long id, @Valid @RequestBody CondominiumUpdateRequest condominiumUpdateRequest
            ) {
        Condominium condominium = CondominiumMapper.updateEntity(condominiumUpdateRequest, new Condominium());

        Condominium updatedCondominium = condominiumService.setUpdateCondominium(condominium, id);

        CondominiumDetailResponse response = new CondominiumDetailResponse(updatedCondominium);

        return ResponseEntity.ok(ResponseUtil.success(response, "Condomínio atualizado com sucesso!"));
    }

    // DELETE
    @DeleteMapping("/{id}")
    public ResponseEntity<ApiResponse<Void>> deleteCondominium(@PathVariable("id") Long idCondominium) {
        condominiumService.setDeleteCondominiumById(idCondominium);

        return ResponseEntity.ok(ResponseUtil.success(null, "Condomínio deletado com sucesso!"));
    }
}
