package com.tcc_vizinhanca.vizinhanca.dto.response.condominium;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonPropertyOrder;
import com.tcc_vizinhanca.vizinhanca.entity.condominium.Condominium;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
@NoArgsConstructor
@JsonPropertyOrder({
        "amount_condominiums",
        "condominiums"
})
public class CondominiumResponse {

    @JsonProperty("amount_condominiums")
    public Integer getAmountCondominiums() {
        return condominiums != null ? condominiums.size() : 0;
    }

    private List<CondominiumDetailResponse> condominiums;

    public CondominiumResponse(List<Condominium> condominiums) {
        this.condominiums = condominiums.stream()
                .map(CondominiumDetailResponse::new)
                .toList();
    }
}
