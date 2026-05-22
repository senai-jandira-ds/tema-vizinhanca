package com.tcc_vizinhanca.vizinhanca.dto.response.condominium_address;

import com.tcc_vizinhanca.vizinhanca.entity.condominium.CondominiumAddress;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class CondominiumAddressResponse {

    private Long id;
    private String cep;
    private String street;
    private String neighborhood;
    private String number;
    private String landmark;
    private String city;
    private String state;

    public CondominiumAddressResponse(CondominiumAddress address) {
        this.id = address.getId();
        this.cep = address.getCep();
        this.street = address.getStreet();
        this.neighborhood = address.getNeighborhood();
        this.number = address.getNumber();
        this.landmark = address.getLandmark();
        this.city = address.getCity();
        this.state = address.getState();
    }
}
