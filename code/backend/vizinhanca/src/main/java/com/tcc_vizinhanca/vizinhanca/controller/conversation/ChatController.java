/***************************************************
 * Objetivo: Controller WebSocket responsável por receber e rotear
 * mensagens do chat em tempo real via STOMP
 * Data: 27/05/2026
 * Autor: Leonardo Scotti
 * Versão: 1.0.05.26
 * *************************************************/

package com.tcc_vizinhanca.vizinhanca.controller.conversation;

import com.tcc_vizinhanca.vizinhanca.dto.request.conversation.MessageRequest;
import com.tcc_vizinhanca.vizinhanca.dto.response.conversation.MessageResponse;
import com.tcc_vizinhanca.vizinhanca.security.jwt.AuthenticatedUser;
import com.tcc_vizinhanca.vizinhanca.service.conversation.ChatService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.messaging.handler.annotation.DestinationVariable;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.handler.annotation.Payload;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.stereotype.Controller;

import java.security.Principal;

@Controller
public class ChatController {

    @Autowired
    private ChatService chatService;


    // Endpoint WebSocket para envio de mensagens.
    // Cliente: /app/chat.send/{conversationId}
    // Broadcast entregue: /topic/conversation.{conversationId}
    @MessageMapping("/chat.send/{conversationId}")
    public void sendMessage(
            @DestinationVariable Long conversationId,
            @Payload MessageRequest request,
            Principal principal) {

        AuthenticatedUser user =
                (AuthenticatedUser) ((UsernamePasswordAuthenticationToken) principal).getPrincipal();

        chatService.sendMessage(conversationId, request.getText(), user.idResident());
    }
}