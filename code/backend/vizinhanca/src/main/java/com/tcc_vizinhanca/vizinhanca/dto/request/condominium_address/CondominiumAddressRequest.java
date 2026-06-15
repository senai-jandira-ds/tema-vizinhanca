package com.tcc_vizinhanca.vizinhanca.dto.request.condominium_address;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class CondominiumAddressRequest {

    @NotBlank(message = "CEP não pode ser vazio")
    @Size(min = 8, max = 9, message = "")
    private String cep;

    @NotBlank(message = "Street não pode ser vazio")
    private String street;

    @NotBlank(message = "Neighborhood não pode ser vazio")
    private String neighborhood;

    @NotBlank(message = "Number não pode ser vazio")
    private String number;

    private String landmark;

    @NotBlank(message = "City não pode ser vazio")
    private String city;

    @NotBlank(message = "State não pode ser vazio")
    @Size(min = 2, max = 2, message = "State tem que ter dois caracteres")
    private String state;

}
