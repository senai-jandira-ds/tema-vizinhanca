/***************************************************
 * Objetivo: DTO de resposta resumido para listagem de conversas,
 * retornando apenas participantes e data, sem mensagens
 * Data: 28/05/2026
 * Autor: Leonardo Scotti
 * Versão: 1.0.05.26
 * *************************************************/

package com.tcc_vizinhanca.vizinhanca.dto.response.conversation;

import com.tcc_vizinhanca.vizinhanca.entity.conversation.Conversation;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;
import java.util.List;

@Getter
@Setter
public class ConversationSummaryResponse {

    private Long id;
    private LocalDateTime createdDate;
    private List<ParticipantResponse> participants;

    public ConversationSummaryResponse(Conversation conversation) {
        this.id           = conversation.getId();
        this.createdDate  = conversation.getCreatedDate();
        this.participants = conversation.getParticipants() != null
                ? conversation.getParticipants().stream()
                .map(ParticipantResponse::new)
                .toList()
                : List.of();
    }
}