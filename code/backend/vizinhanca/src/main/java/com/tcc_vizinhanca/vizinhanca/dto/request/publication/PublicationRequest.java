/***************************************************
 * Objetivo: DTO de requisição responsável por transportar
 * os dados necessários para criação e atualização
 * de uma publicação na aplicação
 * Data: 24/04/2026
 * Autor: Leonardo Scotti
 * Versão: 1.0.04.26
 * *************************************************/

package com.tcc_vizinhanca.vizinhanca.dto.request.publication;

import jakarta.validation.constraints.NotBlank;
import lombok.Getter;
import lombok.Setter;
import org.springframework.web.multipart.MultipartFile;

@Getter
@Setter
public class PublicationRequest {

    @NotBlank(message = "Foto é obrigatória")
    private MultipartFile photo;

    @NotBlank(message = "Título é obrigatório")
    private String title;

    @NotBlank(message = "Descrição é obrigatória")
    private String description;

}
