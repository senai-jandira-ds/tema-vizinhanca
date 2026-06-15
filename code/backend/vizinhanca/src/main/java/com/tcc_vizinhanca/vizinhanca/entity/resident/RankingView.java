package com.tcc_vizinhanca.vizinhanca.entity.resident;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.Getter;
import lombok.Setter;
import org.hibernate.annotations.Immutable;

@Entity
@Immutable
@Table(name = "vw_ranking_geral")
@Getter
@Setter
public class RankingView {
    @Id
    @Column(name = "posicao")
    private Integer posicao;

    @Column(name = "id_condominio")
    private Integer idCondominio;

    @Column(name = "id_morador")
    private Integer idMorador;

    @Column(name = "foto")
    private String photoResident;

    @Column(name = "morador")
    private String nameResident;

    @Column(name = "pontuacao")
    private Integer score;

}
