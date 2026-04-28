package com.tcc_vizinhanca.vizinhanca.dto.response;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonPropertyOrder;
import com.tcc_vizinhanca.vizinhanca.entity.condominium.Condominium;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@JsonPropertyOrder({
        "condos_amount",
        "condos"
})
public class CondominiumResponse {

    @JsonProperty("condos_amount")
    public Integer getCondosAmount() {
        return condos != null ? condos.size() : 0;
    }

    private List<Condominium> condos;

    public CondominiumResponse(List<Condominium> condos) {
        this.condos = condos;
    }
}
