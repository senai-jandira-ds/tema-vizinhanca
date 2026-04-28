package com.tcc_vizinhanca.vizinhanca.dto.response;

import com.tcc_vizinhanca.vizinhanca.entity.resident.Resident;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class ResidentDetailResponse {
    private Long id;
    private String foto;
    private String nome;
    private String apto;
    private String bloco;
    private String cpf;
    private String email;
    private String telefone;
    private Integer pontuacao;

    public ResidentDetailResponse(Resident resident) {
        this.id = resident.getId();
        this.foto = resident.getPhoto();
        this.nome = resident.getName();
        this.apto = resident.getApartment();
        this.bloco = resident.getBlock();
        this.cpf = resident.getCpf();
        this.email = resident.getEmail();
        this.telefone = resident.getPhone();
        this.pontuacao = resident.getScore();
    }
}
