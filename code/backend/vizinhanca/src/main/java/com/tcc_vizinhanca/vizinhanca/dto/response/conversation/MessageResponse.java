/***************************************************
 * Objetivo: DTO de resposta para mensagens enviadas via WebSocket
 * Data: 27/05/2026
 * Autor: Leonardo Scotti
 * Versão: 1.0.05.26
 * *************************************************/

package com.tcc_vizinhanca.vizinhanca.dto.response.conversation;

import com.tcc_vizinhanca.vizinhanca.entity.conversation.Message;
import com.tcc_vizinhanca.vizinhanca.enums.Status;
import com.tcc_vizinhanca.vizinhanca.enums.StatusChat;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;

@Getter
@Setter
public class MessageResponse {

    private Long id;
    private String text;
    private LocalDateTime createdDate;
    private StatusChat status;
    private Long conversationId;
    private Long residentId;
    private String residentName;
    private String residentPhoto;

    public MessageResponse(Message message) {
        this.id             = message.getId();
        this.text           = message.getText();
        this.createdDate    = message.getCreatedDate();
        this.status         = message.getStatus();
        this.conversationId = message.getConversation().getId();
        this.residentId     = message.getResident().getId();
        this.residentName   = message.getResident().getName();
        this.residentPhoto  = message.getResident().getPhoto();
    }
}