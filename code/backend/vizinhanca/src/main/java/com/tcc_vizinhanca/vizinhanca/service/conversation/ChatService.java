/***************************************************
 * Objetivo: Serviço responsável pelo envio e persistência de mensagens
 * do chat em tempo real
 * Data: 27/05/2026
 * Autor: Leonardo Scotti
 * Versão: 1.0.05.26
 * *************************************************/

package com.tcc_vizinhanca.vizinhanca.service.conversation;

import com.tcc_vizinhanca.vizinhanca.dto.response.conversation.MessageResponse;
import com.tcc_vizinhanca.vizinhanca.entity.conversation.Conversation;
import com.tcc_vizinhanca.vizinhanca.entity.conversation.Message;
import com.tcc_vizinhanca.vizinhanca.entity.resident.Resident;
import com.tcc_vizinhanca.vizinhanca.repository.conversation.ConversationRepository;
import com.tcc_vizinhanca.vizinhanca.repository.conversation.MessageRepository;
import com.tcc_vizinhanca.vizinhanca.repository.resident.ResidentRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import java.util.List;

@Service
public class ChatService {

    @Autowired
    private MessageRepository messageRepository;

    @Autowired
    private ConversationRepository conversationRepository;

    @Autowired
    private ResidentRepository residentRepository;

    @Autowired
    private SimpMessagingTemplate messagingTemplate;

    // Persiste a mensagem e faz o broadcast para o tópico da conversa
    public MessageResponse sendMessage(Long conversationId, String text, Long residentId) {
        Conversation conversation = conversationRepository.findById(conversationId)
                .orElseThrow(() -> new ResponseStatusException(
                        HttpStatus.NOT_FOUND, "Conversa não encontrada!"));

        // Valida se o remetente é participante
        boolean isParticipant = conversation.getParticipants().stream()
                .anyMatch(p -> p.getResident().getId().equals(residentId));

        if (!isParticipant) {
            throw new ResponseStatusException(HttpStatus.FORBIDDEN,
                    "Você não faz parte desta conversa!");
        }

        Resident resident = residentRepository.findById(residentId)
                .orElseThrow(() -> new ResponseStatusException(
                        HttpStatus.NOT_FOUND, "Morador não encontrado!"));

        Message message = new Message();
        message.setText(text);
        message.setConversation(conversation);
        message.setResident(resident);

        Message saved = messageRepository.save(message);
        MessageResponse response = new MessageResponse(saved);

        // Broadcast para todos os assinantes do tópico da conversa
        messagingTemplate.convertAndSend(
                "/topic/conversation." + conversationId, response);

        return response;
    }

    // Retorna o histórico de mensagens de uma conversa
    public List<MessageResponse> getMessages(Long conversationId) {
        return messageRepository.findByConversationIdOrderByCreatedDateAsc(conversationId)
                .stream()
                .map(MessageResponse::new)
                .toList();
    }
}