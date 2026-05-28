/***************************************************
 * Objetivo: Serviço responsável pela regra de negócio das conversas,
 * incluindo criação, busca e listagem por morador
 * Data: 27/05/2026
 * Autor: Leonardo Scotti
 * Versão: 1.0.05.26
 * *************************************************/

package com.tcc_vizinhanca.vizinhanca.service.conversation;

import com.tcc_vizinhanca.vizinhanca.entity.conversation.Conversation;
import com.tcc_vizinhanca.vizinhanca.entity.conversation.ConversationParticipant;
import com.tcc_vizinhanca.vizinhanca.entity.conversation.ConversationParticipantId;
import com.tcc_vizinhanca.vizinhanca.entity.resident.Resident;
import com.tcc_vizinhanca.vizinhanca.repository.conversation.ConversationParticipantRepository;
import com.tcc_vizinhanca.vizinhanca.repository.conversation.ConversationRepository;
import com.tcc_vizinhanca.vizinhanca.service.resident.ResidentService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import java.util.List;

@Service
public class ConversationService {

    @Autowired
    private ConversationRepository conversationRepository;

    @Autowired
    private ConversationParticipantRepository conversationParticipantRepository;

    @Autowired
    private ResidentService residentService;

    // Lista todas as conversas de um morador
    public List<Conversation> getConversationsByResidentId(Long residentId) {
        return conversationParticipantRepository
                .findConversationsByResidentId(residentId);
    }

    // Busca conversa por ID — valida se o morador é participante
    public Conversation getConversationById(Long conversationId, Long residentId) {
        Conversation conversation = conversationRepository.findById(conversationId)
                .orElseThrow(() -> new ResponseStatusException(
                        HttpStatus.NOT_FOUND, "Conversa não encontrada!"));

        boolean isParticipant = conversation.getParticipants().stream()
                .anyMatch(p -> p.getResident().getId().equals(residentId));

        if (!isParticipant) {
            throw new ResponseStatusException(HttpStatus.FORBIDDEN,
                    "Você não faz parte desta conversa!");
        }

        return conversation;
    }

    // Cria uma nova conversa entre dois moradores (ou retorna a existente)
    public Conversation createOrGetConversation(Long initiatorId, Long targetId) {
        if (initiatorId.equals(targetId)) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST,
                    "Não é possível iniciar uma conversa consigo mesmo!");
        }

        // Verifica se já existe conversa entre os dois
        List<Conversation> existing =
                conversationParticipantRepository.findCommonConversation(initiatorId, targetId);

        if (!existing.isEmpty()) {
            return existing.get(0);
        }

        Resident initiator = residentService.getSelectResidentById(initiatorId);
        Resident target    = residentService.getSelectResidentById(targetId);

        Conversation conversation = new Conversation();
        conversation = conversationRepository.save(conversation);

        ConversationParticipant p1 = new ConversationParticipant();
        p1.setId(new ConversationParticipantId(conversation.getId(), initiatorId));
        p1.setConversation(conversation);
        p1.setResident(initiator);

        ConversationParticipant p2 = new ConversationParticipant();
        p2.setId(new ConversationParticipantId(conversation.getId(), targetId));
        p2.setConversation(conversation);
        p2.setResident(target);

        conversationParticipantRepository.saveAll(List.of(p1, p2));

        return conversationRepository.findById(conversation.getId()).orElseThrow();
    }
}