package com.tcc_vizinhanca.vizinhanca.entity.condominium;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.Getter;
import lombok.Setter;
import org.hibernate.annotations.Immutable;

import java.time.LocalDateTime;

@Entity
@Immutable
@Table(name = "vw_atividades_morador")
@Getter
@Setter
public class ActivityView {

    @Id
    @Column(name = "activity_id")
    private Long id;

    @Column(name = "id_morador")
    private Long idMorador;

    @Column(name = "id_condominio")
    private Long idCondominium;

    @Column(name = "morador")
    private String resident;

    @Column(name = "tipo")
    private String type;

    @Column(name = "object_id")
    private Long idObject;

    @Column(name = "service_id")
    private Long idService;

    @Column(name = "report_id")
    private Long idReport;

    @Column(name = "descricao")
    private String description;

    private String status;

    @Column(name = "data_criacao")
    private LocalDateTime dataCriacao;

}
