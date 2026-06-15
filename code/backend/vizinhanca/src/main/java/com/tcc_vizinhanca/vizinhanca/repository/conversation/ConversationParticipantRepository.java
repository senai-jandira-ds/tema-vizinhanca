/***************************************************
 * Objetivo: Repositório de participantes de conversas com queries
 * para buscar conversas por morador e conversas em comum
 * Data: 27/05/2026
 * Autor: Leonardo Scotti
 * Versão: 1.0.05.26
 * *************************************************/

package com.tcc_vizinhanca.vizinhanca.repository.conversation;

import com.tcc_vizinhanca.vizinhanca.entity.conversation.Conversation;
import com.tcc_vizinhanca.vizinhanca.entity.conversation.ConversationParticipant;
import com.tcc_vizinhanca.vizinhanca.entity.conversation.ConversationParticipantId;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface ConversationParticipantRepository
        extends JpaRepository<ConversationParticipant, ConversationParticipantId> {

    // Todas as conversas de um morador com participantes e mensagens carregados
    @Query("""
        SELECT DISTINCT c FROM Conversation c
        JOIN c.participants p
        LEFT JOIN FETCH c.participants cp
        LEFT JOIN FETCH cp.resident
        WHERE p.resident.id = :residentId
        ORDER BY c.createdDate DESC
    """)
    List<Conversation> findConversationsByResidentId(@Param("residentId") Long residentId);

    // Conversa existente entre dois moradores específicos
    @Query("""
        SELECT c FROM Conversation c
        JOIN c.participants p1 ON p1.resident.id = :id1
        JOIN c.participants p2 ON p2.resident.id = :id2
    """)
    List<Conversation> findCommonConversation(
            @Param("id1") Long id1,
            @Param("id2") Long id2);
}