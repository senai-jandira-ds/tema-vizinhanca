/***************************************************
 * Objetivo: Mapper responsável pela conversão entre
 * os DTOs de requisição e a entidade Object,
 * utilizado nas operações de criação e atualização
 * Data: 26/05/2026
 * Autor: Leonardo Scotti
 * Versão: 1.0.05.26
 * *************************************************/

package com.tcc_vizinhanca.vizinhanca.mapper.object;

import com.tcc_vizinhanca.vizinhanca.dto.request.object.ObjectCreateRequest;
import com.tcc_vizinhanca.vizinhanca.dto.request.object.ObjectUpdateRequest;
import com.tcc_vizinhanca.vizinhanca.entity.object.Object;
import com.tcc_vizinhanca.vizinhanca.enums.StatusObject;
import org.springframework.cglib.core.Local;

import java.time.LocalDate;

public class ObjectMapper {

    public static Object toEntity(ObjectCreateRequest dto) {
        Object object = new Object();
        object.setTitle(dto.getTitle());
        object.setDeadline(dto.getDeadline());
        object.setDescription(dto.getDescription());
        object.setStatus(StatusObject.valueOf(dto.getStatus()));
        return object;
    }

    public static Object updateEntity(ObjectUpdateRequest dto, Object entity) {
        if (dto.getTitle() != null) entity.setTitle(dto.getTitle());
        if (dto.getDeadline() != null) LocalDate.parse(dto.getDeadline());
        if (dto.getDescription() != null) entity.setDescription(dto.getDescription());
        if (dto.getStatus() != null) entity.setStatus(StatusObject.valueOf(dto.getStatus()));
        return entity;
    }
}
