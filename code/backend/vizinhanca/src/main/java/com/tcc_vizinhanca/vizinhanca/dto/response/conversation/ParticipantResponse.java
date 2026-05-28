/***************************************************
 * Objetivo: DTO de resposta para participantes de uma conversa
 * Data: 27/05/2026
 * Autor: Leonardo Scotti
 * Versão: 1.0.05.26
 * *************************************************/

package com.tcc_vizinhanca.vizinhanca.dto.response.conversation;

import com.tcc_vizinhanca.vizinhanca.entity.conversation.ConversationParticipant;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class ParticipantResponse {

    private Long residentId;
    private String residentName;
    private String residentPhoto;

    public ParticipantResponse(ConversationParticipant participant) {
        this.residentId    = participant.getResident().getId();
        this.residentName  = participant.getResident().getName();
        this.residentPhoto = participant.getResident().getPhoto();
    }
}