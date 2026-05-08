package com.tcc_vizinhanca.vizinhanca.dto.response.resident;

import com.tcc_vizinhanca.vizinhanca.entity.resident.Resident;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class ResidentSummaryResponse {
    private Long id;
    private String photo;
    private String name;
    private String apartment;
    private String block;
    private String cpf;
    private String email;
    private String phone;
    private Integer score;

    public ResidentSummaryResponse(Resident resident) {
        this.id = resident.getId();
        this.photo = resident.getPhoto();
        this.name = resident.getName();
        this.apartment = resident.getApartment();
        this.block = resident.getBlock();
        this.cpf = resident.getCpf();
        this.email = resident.getEmail();
        this.phone = resident.getPhone();
        this.score = resident.getScore();
    }
}
