package com.tcc_vizinhanca.vizinhanca.dto.response.object;

import com.tcc_vizinhanca.vizinhanca.dto.response.service.ServiceDetailResponse;
import com.tcc_vizinhanca.vizinhanca.entity.object.Object;
import com.tcc_vizinhanca.vizinhanca.entity.service.Service;
import lombok.Getter;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
public class ObjectResponse {

    private Integer amount_objects;
    private List<ObjectDetailResponse> objects;

    public ObjectResponse(List<Object> objects) {
        this.amount_objects = objects.size();
        this.objects = objects.stream()
                .map(ObjectDetailResponse::new)
                .toList();
    }
}
