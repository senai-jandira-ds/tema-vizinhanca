package com.tcc_vizinhanca.vizinhanca.dto.response.service;

import com.tcc_vizinhanca.vizinhanca.dto.response.category.CategorySummaryResponse;
import com.tcc_vizinhanca.vizinhanca.dto.response.resident.ResidentSummaryResponse;
import com.tcc_vizinhanca.vizinhanca.entity.service.Service;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class ServiceSummaryResponse {

    private Long id;
    private String photo;
    private String title;
    private Integer estimatedTime;
    private String urgency;
    private String description;
    private String status;
    private Long resident;
    private CategorySummaryResponse category;

    public ServiceSummaryResponse(Service service) {
        this.id = service.getId();
        this.photo = service.getPhoto();
        this.title = service.getTitle();
        this.estimatedTime = service.getEstimatedTime();
        this.urgency = service.getUrgency();
        this.description = service.getDescription();
        this.status = service.getStatus();
        this.resident = service.getResident().getId();
        this.category = service.getCategory() != null
                ? new CategorySummaryResponse(service.getCategory())
                : new CategorySummaryResponse();
    }
}
