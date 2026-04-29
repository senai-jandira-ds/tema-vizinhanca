package com.tcc_vizinhanca.vizinhanca.controller.condominium;

import com.tcc_vizinhanca.vizinhanca.dto.request.condominium.CondominiumCreateRequest;
import com.tcc_vizinhanca.vizinhanca.dto.request.condominium.CondominiumUpdateRequest;
import com.tcc_vizinhanca.vizinhanca.dto.response.ApiResponse;
import com.tcc_vizinhanca.vizinhanca.dto.response.condominium.CondominiumDetailResponse;
import com.tcc_vizinhanca.vizinhanca.dto.response.condominium.CondominiumResponse;
import com.tcc_vizinhanca.vizinhanca.entity.condominium.Condominium;
import com.tcc_vizinhanca.vizinhanca.mapper.CondominiumMapper;
import com.tcc_vizinhanca.vizinhanca.service.condominium.CondominiumService;
import com.tcc_vizinhanca.vizinhanca.util.ResponseUtil;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/v1/condominium")
public class CondominiumController {

    @Autowired
    private CondominiumService condominiumService;

    // GET ALL
    @GetMapping
    public ResponseEntity<ApiResponse<CondominiumResponse>> listAllCondos() {
        System.out.println("CondominiumController: listAllCondos");
        List<Condominium> condos = condominiumService.getSelectAllCondominiums();

        CondominiumResponse response = new CondominiumResponse(condos);

        return ResponseEntity.ok(ResponseUtil.success(response, "Lista de condomínios retornada com sucesso!"));
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
        System.out.println("ENTROU");
        Condominium condominium = CondominiumMapper.updateEntity(condominiumUpdateRequest, new Condominium());

        Condominium updatedCondominium = condominiumService.setUpdateCondominium(condominium, id);

        CondominiumDetailResponse response = new CondominiumDetailResponse(updatedCondominium);
        System.out.println(response.toString());
        return ResponseEntity.ok(ResponseUtil.success(response, "Condomínio atualizado com sucesso!"));
    }

    // DELETE
    @DeleteMapping("/{id}")
    public ResponseEntity<ApiResponse<Void>> deleteCondominium(@PathVariable("id") Long idCondominium) {
        condominiumService.setDeleteCondominiumById(idCondominium);

        return ResponseEntity.ok(ResponseUtil.success(null, "Condomínio deletado com sucesso!"));
    }
}
