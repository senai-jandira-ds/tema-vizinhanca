package com.tcc_vizinhanca.vizinhanca.dto.response.condominium;

import com.tcc_vizinhanca.vizinhanca.entity.condominium.ActivityView;
import lombok.Getter;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
public class ActivityViewResponse {

    List<ActivityViewDetailResponse> activities;

    public ActivityViewResponse(List<ActivityView> activities) {
        this.activities = activities.stream()
                .map(ActivityViewDetailResponse::new)
                .toList();
    }
}
