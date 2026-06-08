/***************************************************
 * Objetivo: DTO responsável por retornar os dados detalhados de um objeto
 * Data: 26/05/2026
 * Autor: Leonardo Scotti
 * Versão: 1.0.05.26
 * *************************************************/

package com.tcc_vizinhanca.vizinhanca.dto.response.object;

import com.tcc_vizinhanca.vizinhanca.dto.response.category.CategorySummaryResponse;
import com.tcc_vizinhanca.vizinhanca.dto.response.resident.ResidentSummaryResponse;
import com.tcc_vizinhanca.vizinhanca.entity.object.Object;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDate;
import java.time.LocalDateTime;

@Getter
@Setter
public class ObjectDetailResponse {

    private Long id;
    private String photo;
    private String title;
    private LocalDate deadline;
    private String description;
    private LocalDateTime creation_date;
    private String status;
    private ResidentSummaryResponse resident;
    private CategorySummaryResponse category;

    public ObjectDetailResponse(Object object) {
        this.id = object.getId();
        this.photo = object.getPhoto();
        this.title = object.getTitle();
        this.deadline = object.getDeadline();
        this.description = object.getDescription();
        this.creation_date = object.getCreationDate();
        this.status = object.getStatus().toString();
        this.resident = object.getResident() != null
                ? new ResidentSummaryResponse(object.getResident())
                : new ResidentSummaryResponse();
        this.category = object.getCategory() != null
                ? new CategorySummaryResponse(object.getCategory())
                : new CategorySummaryResponse();
    }
}