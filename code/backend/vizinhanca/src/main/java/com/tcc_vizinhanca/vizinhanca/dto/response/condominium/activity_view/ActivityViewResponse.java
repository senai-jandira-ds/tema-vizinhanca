package com.tcc_vizinhanca.vizinhanca.dto.response.condominium.activity_view;

import com.tcc_vizinhanca.vizinhanca.entity.condominium.ActivityView;
import lombok.Getter;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
public class ActivityViewResponse {

    private Integer amount_activity;
    private List<ActivityViewDetailResponse> activities;

    public ActivityViewResponse(List<ActivityView> activities) {
        this.amount_activity = activities.size();
        this.activities = activities.stream()
                .map(ActivityViewDetailResponse::new)
                .toList();
    }
}
