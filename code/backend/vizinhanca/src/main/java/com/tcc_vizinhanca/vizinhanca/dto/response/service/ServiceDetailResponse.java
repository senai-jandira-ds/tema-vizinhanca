/***************************************************
 * Objetivo: DTO responsável por retornar os dados detalhados de um serviço
 * Data: 15/05/2026
 * Autor: Leonardo Scotti
 * Versão: 1.0.05.26
 * *************************************************/

package com.tcc_vizinhanca.vizinhanca.dto.response.service;

import com.tcc_vizinhanca.vizinhanca.dto.response.category.CategorySummaryResponse;
import com.tcc_vizinhanca.vizinhanca.entity.service.Service;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
public class ServiceDetailResponse {

    private Long id;
    private String photo;
    private String title;
    private Integer estimatedTime;
    private String urgency;
    private String description;
    private String status;
    private Long residentId;
    private String residentName;
    private String residentPhoto;
    private CategorySummaryResponse category;

    public ServiceDetailResponse(Service service) {
        this.id = service.getId();
        this.photo = service.getPhoto();
        this.title = service.getTitle();
        this.estimatedTime = service.getEstimatedTime();
        this.urgency = service.getUrgency();
        this.description = service.getDescription();
        this.status = service.getStatus();
        this.residentId = service.getResident().getId();
        this.residentName = service.getResident().getName();
        this.residentPhoto = service.getResident().getPhoto();
        this.category = service.getCategory() != null
                ? new CategorySummaryResponse(service.getCategory())
                : null;
    }
}