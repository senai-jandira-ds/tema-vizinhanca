/***************************************************
 * Objetivo: DTO responsável por retornar os dados resumidos de um objeto,
 * utilizado em listagens e relacionamentos
 * Data: 26/05/2026
 * Autor: Leonardo Scotti
 * Versão: 1.0.05.26
 * *************************************************/

package com.tcc_vizinhanca.vizinhanca.dto.response.object;

import com.tcc_vizinhanca.vizinhanca.dto.response.category.CategorySummaryResponse;
import com.tcc_vizinhanca.vizinhanca.entity.object.Object;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDate;

@Getter
@Setter
@NoArgsConstructor
public class ObjectSummaryResponse {

    private Long id;
    private String photo;
    private String title;
    private LocalDate deadline;
    private String status;
    private String resident_name;
    private String resident_apartment;
    private String resident_cpf;
    private CategorySummaryResponse category;

    public ObjectSummaryResponse(Object object) {
        this.id = object.getId();
        this.photo = object.getPhoto();
        this.title = object.getTitle();
        this.deadline = object.getDeadline();
        this.status = object.getStatus().toString();
        this.resident_name = object.getResident() != null
                ? object.getResident().getName()
                : "";
        this.resident_apartment = object.getResident() != null
                ? object.getResident().getApartment() + " - " + object.getResident().getBlock()
                : "";
        this.resident_cpf = object.getResident() != null
                ? object.getResident().getCpf()
                : "";
        this.category = object.getCategory() != null
                ? new CategorySummaryResponse(object.getCategory())
                : new CategorySummaryResponse();
    }
}