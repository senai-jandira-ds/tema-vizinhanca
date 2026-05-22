package com.tcc_vizinhanca.vizinhanca.controller.resident;

import com.tcc_vizinhanca.vizinhanca.dto.request.resident.ResidentCreateRequest;
import com.tcc_vizinhanca.vizinhanca.dto.request.resident.ResidentUpdateRequest;
import com.tcc_vizinhanca.vizinhanca.dto.response.ApiResponse;
import com.tcc_vizinhanca.vizinhanca.dto.response.resident.ResidentDetailResponse;
import com.tcc_vizinhanca.vizinhanca.dto.response.resident.ResidentResponse;
import com.tcc_vizinhanca.vizinhanca.entity.condominium.Block;
import com.tcc_vizinhanca.vizinhanca.entity.condominium.Condominium;
import com.tcc_vizinhanca.vizinhanca.entity.resident.Resident;
import com.tcc_vizinhanca.vizinhanca.mapper.resident.ResidentMapper;
import com.tcc_vizinhanca.vizinhanca.service.block.BlockService;
import com.tcc_vizinhanca.vizinhanca.service.condominium.CondominiumService;
import com.tcc_vizinhanca.vizinhanca.service.resident.ResidentService;
import com.tcc_vizinhanca.vizinhanca.util.ResponseUtil;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
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

    // GET ALL
    @GetMapping
    public ResponseEntity<ApiResponse<ResidentResponse>> listAllResidents() {

        List<Resident> residents = residentService.getSelectAllResidents();

        ResidentResponse response = new ResidentResponse(residents);

        return ResponseEntity.ok(ResponseUtil.success(response, "Lista de Moradores retornada com sucesso!"));
    }

    // GET BY ID
    @GetMapping("/{id}")
    public ResponseEntity<ApiResponse<ResidentDetailResponse>> searchResidentById(@PathVariable("id") Long idResident) {

        Resident resident = residentService.getSelectResidentById(idResident);

        ResidentDetailResponse response = new ResidentDetailResponse(resident);

        return ResponseEntity.ok(ResponseUtil.success(response, "Morador encontrado com sucesso!"));

    }


    // POST
    @PostMapping
    public ResponseEntity<ApiResponse<ResidentDetailResponse>> insertResident(@Valid @RequestBody ResidentCreateRequest residentCreateRequest) {

        Condominium condominium = condominiumService.getSelectCondominiumById(residentCreateRequest.getIdCondominium());

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
            @Valid @ModelAttribute ResidentUpdateRequest residentUpdateRequest
            ) {

        Resident updatedResident = residentService.setUpdateResident(residentUpdateRequest, residentUpdateRequest.getPhoto(),id);

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
