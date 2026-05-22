package com.tcc_vizinhanca.vizinhanca.dto.response.resident;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonPropertyOrder;
import com.tcc_vizinhanca.vizinhanca.entity.resident.Resident;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
@NoArgsConstructor
@JsonPropertyOrder({
        "amount_residents",
        "residents"
})
public class ResidentResponse {

    @JsonProperty("amount_residents")
    public Integer getAmountResidents() {
        return residents != null ? residents.size() : 0;
    }

    private List<ResidentDetailResponse> residents;

    public ResidentResponse(List<Resident> residents) {
        this.residents = residents.stream()
                .map(ResidentDetailResponse::new)
                .toList();
    }
}
