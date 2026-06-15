/***************************************************
 * Objetivo: DTO de resposta com detalhes de uma conversa
 * Data: 27/05/2026
 * Autor: Leonardo Scotti
 * Versão: 1.0.05.26
 * *************************************************/

package com.tcc_vizinhanca.vizinhanca.dto.response.conversation;

import com.tcc_vizinhanca.vizinhanca.entity.conversation.Conversation;
import com.tcc_vizinhanca.vizinhanca.entity.conversation.ConversationParticipant;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;
import java.util.List;

@Getter
@Setter
public class ConversationDetailResponse {

    private Long id;
    private LocalDateTime createdDate;
    private List<ParticipantResponse> participants;
    private List<MessageResponse> messages;

    public ConversationDetailResponse(Conversation conversation) {
        this.id          = conversation.getId();
        this.createdDate = conversation.getCreatedDate();

        this.participants = conversation.getParticipants() != null
                ? conversation.getParticipants().stream()
                .map(ParticipantResponse::new)
                .toList()
                : List.of();

        this.messages = conversation.getMessages() != null
                ? conversation.getMessages().stream()
                .map(MessageResponse::new)
                .toList()
                : List.of();
    }
}