/***************************************************
 * Objetivo: Controller REST responsável pelos endpoints de conversas:
 * criação, listagem e histórico de mensagens
 * Data: 27/05/2026
 * Autor: Leonardo Scotti
 * Versão: 1.0.05.26
 * *************************************************/

package com.tcc_vizinhanca.vizinhanca.controller.conversation;

import com.tcc_vizinhanca.vizinhanca.dto.request.conversation.ConversationRequest;
import com.tcc_vizinhanca.vizinhanca.dto.response.ApiResponse;
import com.tcc_vizinhanca.vizinhanca.dto.response.conversation.ConversationDetailResponse;
import com.tcc_vizinhanca.vizinhanca.dto.response.conversation.MessageResponse;
import com.tcc_vizinhanca.vizinhanca.entity.conversation.Conversation;
import com.tcc_vizinhanca.vizinhanca.security.jwt.AuthenticatedUser;
import com.tcc_vizinhanca.vizinhanca.service.conversation.ChatService;
import com.tcc_vizinhanca.vizinhanca.service.conversation.ConversationService;
import com.tcc_vizinhanca.vizinhanca.util.ResponseUtil;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/v1/conversation")
@Tag(name = "Conversation", description = "Endpoints para gerenciamento das conversas.")
public class ConversationController {

    @Autowired
    private ConversationService conversationService;

    @Autowired
    private ChatService chatService;

    // GET ALL
    @GetMapping
    public ResponseEntity<ApiResponse<List<ConversationDetailResponse>>> listMyConversations() {
        AuthenticatedUser user =
                (AuthenticatedUser) SecurityContextHolder.getContext().getAuthentication().getPrincipal();

        List<ConversationDetailResponse> response =
                conversationService.getConversationsByResidentId(user.idResident())
                        .stream()
                        .map(ConversationDetailResponse::new)
                        .toList();

        return ResponseEntity.ok(ResponseUtil.success(response, "Conversas retornadas com sucesso!"));
    }

    // GET BY ID
    @GetMapping("/{id}")
    public ResponseEntity<ApiResponse<ConversationDetailResponse>> getConversationById(
            @PathVariable Long id) {

        AuthenticatedUser user =
                (AuthenticatedUser) SecurityContextHolder.getContext().getAuthentication().getPrincipal();

        Conversation conversation =
                conversationService.getConversationById(id, user.idResident());

        return ResponseEntity.ok(ResponseUtil.success(
                new ConversationDetailResponse(conversation),
                "Conversa encontrada com sucesso!"));
    }

    // GET MESSAGES BY ID
    @GetMapping("/{id}/messages")
    public ResponseEntity<ApiResponse<List<MessageResponse>>> getMessages(
            @PathVariable Long id) {

        AuthenticatedUser user =
                (AuthenticatedUser) SecurityContextHolder.getContext().getAuthentication().getPrincipal();

        // Valida participação antes de retornar mensagens
        conversationService.getConversationById(id, user.idResident());

        List<MessageResponse> messages = chatService.getMessages(id);

        return ResponseEntity.ok(ResponseUtil.success(messages, "Mensagens retornadas com sucesso!"));
    }

    // Cria ou retorna conversa existente entre dois moradores
    @PostMapping
    public ResponseEntity<ApiResponse<ConversationDetailResponse>> createConversation(
            @Valid @RequestBody ConversationRequest request) {

        AuthenticatedUser user =
                (AuthenticatedUser) SecurityContextHolder.getContext().getAuthentication().getPrincipal();

        Conversation conversation =
                conversationService.createOrGetConversation(
                        user.idResident(), request.getTargetResidentId());

        return ResponseEntity.status(HttpStatus.CREATED)
                .body(ResponseUtil.success(
                        new ConversationDetailResponse(conversation),
                        "Conversa criada com sucesso!"));
    }
}