package com.tcc_vizinhanca.vizinhanca.entity.condominium;

import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name = "tbl_bloco")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Block {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id_bloco", nullable = false)
    private Long id;

    @Column(name = "bloco", nullable = false)
    private String block;

    @ManyToOne
    @JoinColumn(name = "id_condominio", nullable = false)
    private Condominium condominium;
}
