package com.tcc_vizinhanca.vizinhanca.dto.response.resident;

import com.tcc_vizinhanca.vizinhanca.dto.response.block.BlockSummaryResponse;
import com.tcc_vizinhanca.vizinhanca.dto.response.publication.PublicationSummaryResponse;
import com.tcc_vizinhanca.vizinhanca.entity.resident.Resident;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.ArrayList;
import java.util.List;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class ResidentSummaryResponse {
    private Long id;
    private String photo;
    private String name;
    private String apartment;
    private String cpf;
    private String email;
    private String phone;
    private Integer score;
    private String creation_date;
    private BlockSummaryResponse block;

    public ResidentSummaryResponse(Resident resident) {
        this.id = resident.getId();
        this.photo = resident.getPhoto();
        this.name = resident.getName();
        this.apartment = resident.getApartment();
        this.cpf = resident.getCpf();
        this.email = resident.getEmail();
        this.phone = resident.getPhone();
        this.score = resident.getScore();
        this.creation_date = resident.getCreationDate() != null
                ? resident.getCreationDate().toString()
                : "";
        this.block = resident.getBlock() != null
                ? new BlockSummaryResponse(resident.getBlock())
                : null;
    }
}
