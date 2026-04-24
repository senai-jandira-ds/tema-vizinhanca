/***************************************************
 * Objetivo: Serviço responsável pelas regras de negócio relacionadas
 * à entidade Resident, incluindo operações de CRUD, validações
 * e regras de gerenciamento de moradores
 * Data: 24/04/2026
 * Autor: Leonardo Scotti
 * Versão: 1.0.04.26
 * *************************************************/

package com.tcc_vizinhanca.vizinhanca.service.resident;

import com.tcc_vizinhanca.vizinhanca.entity.resident.Resident;
import com.tcc_vizinhanca.vizinhanca.repository.resident.ResidentRepository;
import lombok.NonNull;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import java.util.List;

@Service
public class ResidentService {

    @Autowired
    private ResidentRepository residentRepository;

    // SELECT ALL
    public List<Resident> getSelectAllResidents(){
        return residentRepository.findAll();
    }

    // SELECT BY ID
    public Resident getSelectResidentById(@NonNull Long id){
        Resident resident = residentRepository.findById(id)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Morador não encontrado no Banco de Dados!"));

        return resident;
    }

    // INSERT RESIDENT
    public Resident setInsertResident(@NonNull Resident resident){
        resident.setId(null);

        return residentRepository.save(resident);
    }

    // UPDATE RESIDENT
    public Resident setUpdateResident(@NonNull Resident resident, Long idResident){
        Resident existingResident = getSelectResidentById(idResident);

        BeanUtils.copyProperties(resident, existingResident, "id");

        return residentRepository.save(existingResident);
    }

    // DELETE RESIDENT
    public void setDeleteResidentById(Long idResident){
        if (!residentRepository.existsById(idResident)) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Morador não  encontrado no Banco de Dados!");
        }

        residentRepository.deleteById(idResident);
    }
}
