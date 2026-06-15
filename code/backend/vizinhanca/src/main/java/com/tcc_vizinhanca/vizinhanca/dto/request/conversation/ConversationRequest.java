/***************************************************
 * Objetivo: DTO de entrada para criação de uma nova conversa,
 * recebendo o ID do morador destinatário
 * Data: 27/05/2026
 * Autor: Leonardo Scotti
 * Versão: 1.0.05.26
 * *************************************************/

package com.tcc_vizinhanca.vizinhanca.dto.request.conversation;

import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class ConversationRequest {

    @NotNull(message = "ID do destinatário é obrigatório")
    private Long targetResidentId;
}