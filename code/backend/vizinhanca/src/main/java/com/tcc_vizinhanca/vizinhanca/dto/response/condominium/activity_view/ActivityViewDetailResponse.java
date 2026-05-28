package com.tcc_vizinhanca.vizinhanca.dto.response.condominium.activity_view;

import com.tcc_vizinhanca.vizinhanca.entity.condominium.ActivityView;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDateTime;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class ActivityViewDetailResponse {

    private Long resident_id;
    private String resident_name;
    private String type;
    private String description;
    private String status;
    private LocalDateTime creation_date;

    public ActivityViewDetailResponse(ActivityView view) {
        this.resident_id = view.getIdMorador();
        this.resident_name = view.getResident();
        this.type = view.getType();
        this.description = view.getDescription();
        this.status = view.getStatus();
        this.creation_date = view.getDataCriacao();
    }

}
