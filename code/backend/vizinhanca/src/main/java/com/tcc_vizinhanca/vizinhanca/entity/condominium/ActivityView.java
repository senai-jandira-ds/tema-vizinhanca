package com.tcc_vizinhanca.vizinhanca.entity.condominium;

import com.azure.core.annotation.Immutable;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;

@Entity
@Immutable
@Table(name = "vw_atividades_morador")
@Getter
@Setter
public class ActivityView {

    @Id
    @Column(name = "id_morador")
    private Long idMorador;

    @Column(name = "id_condominio")
    private Long idCondominium;

    @Column(name = "morador")
    private String resident;

    @Column(name = "tipo")
    private String type;

    @Column(name = "descricao")
    private String description;

    private String status;

    @Column(name = "data_criacao")
    private LocalDateTime dataCriacao;

}
