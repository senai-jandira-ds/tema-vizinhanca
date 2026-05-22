package com.tcc_vizinhanca.vizinhanca.dto.response.condominium;

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

    private Long idMorador;
    private String morador;
    private String tipo;
    private String descricao;
    private String status;
    private LocalDateTime dataCriacao;

    public ActivityViewDetailResponse(ActivityView view) {
        this.idMorador = view.getIdMorador();
        this.morador = view.getResident();
        this.tipo = view.getType();
        this.descricao = view.getDescription();
        this.status = view.getStatus();
        this.dataCriacao = view.getDataCriacao();
    }

}
