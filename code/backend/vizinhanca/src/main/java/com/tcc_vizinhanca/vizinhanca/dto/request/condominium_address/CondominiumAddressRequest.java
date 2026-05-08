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

    @NotBlank(message = "Street is required")
    private String street;

    @NotBlank(message = "Neighborhood is required")
    private String neighborhood;

    @NotBlank(message = "Number is required")
    private String number;

    @NotBlank(message = "Landmark is required")
    private String landmark;

    @NotBlank(message = "City is required")
    private String city;

    @NotBlank(message = "State is required")
    @Size(min = 2, max = 2, message = "State must have 2 characters")
    private String state;

}
