/***************************************************
 * Objetivo: DTO responsável por retornar a lista de serviços
 * Data: 15/05/2026
 * Autor: Leonardo Scotti
 * Versão: 1.0.05.26
 * *************************************************/

package com.tcc_vizinhanca.vizinhanca.dto.response.service;

import com.tcc_vizinhanca.vizinhanca.entity.service.Service;
import lombok.Getter;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
public class ServiceResponse {

    private Integer amountServices;
    private List<ServiceDetailResponse> services;

    public ServiceResponse(List<Service> services) {
        this.amountServices = services.size();
        this.services = services.stream()
                .map(ServiceDetailResponse::new)
                .toList();
    }
}