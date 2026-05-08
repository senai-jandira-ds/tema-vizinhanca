package com.tcc_vizinhanca.vizinhanca.entity.condominium;

import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name = "tbl_endereco_condominio")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class CondominiumAddress {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id_endereco_condominio")
    private Long id;

    @Column(name = "cep", nullable = false, length = 9)
    private String cep;

    @Column(name = "logradouro", nullable = false, length = 120)
    private String street;

    @Column(name = "bairro", nullable = false, length = 120)
    private String neighborhood;

    @Column(name = "numero", nullable = false, length = 10)
    private String number;

    @Column(name = "ponto_referencia", nullable = false, length = 120)
    private String landmark;

    @Column(name = "cidade", nullable = false, length = 30)
    private String city;

    @Column(name = "estado", nullable = false, length = 2)
    private String state;

    @OneToOne
    @JoinColumn(name = "id_condominio", nullable = false)
    private Condominium condominium;
}
