/***************************************************
 * Objetivo: Controller REST responsável pelos endpoints de notificações:
 * listagem, contagem de não lidas e marcação como lida
 * Data: 31/05/2026
 * Autor: Leonardo Scotti
 * Versão: 1.0.05.26
 * *************************************************/

package com.tcc_vizinhanca.vizinhanca.controller.notification;

import com.tcc_vizinhanca.vizinhanca.dto.response.ApiResponse;
import com.tcc_vizinhanca.vizinhanca.dto.response.notification.NotificationResponse;
import com.tcc_vizinhanca.vizinhanca.security.jwt.AuthenticatedUser;
import com.tcc_vizinhanca.vizinhanca.service.notification.NotificationService;
import com.tcc_vizinhanca.vizinhanca.util.ResponseUtil;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/v1/notification")
@Tag(name = "Notification", description = "Endpoints para gerenciamento de notificações.")
public class NotificationController {

    @Autowired
    private NotificationService notificationService;

    // Todas as notificações do morador autenticado
    @GetMapping
    public ResponseEntity<ApiResponse<List<NotificationResponse>>> listAll() {
        AuthenticatedUser user = authenticatedUser();

        return ResponseEntity.ok(ResponseUtil.success(
                notificationService.getByResidentId(user.idResident()),
                "Notificações retornadas com sucesso!"));
    }

    // Apenas não lidas
    @GetMapping("/unread")
    public ResponseEntity<ApiResponse<List<NotificationResponse>>> listUnread() {
        AuthenticatedUser user = authenticatedUser();

        return ResponseEntity.ok(ResponseUtil.success(
                notificationService.getUnreadByResidentId(user.idResident()),
                "Notificações não lidas retornadas com sucesso!"));
    }

    // Contagem de não lidas
    @GetMapping("/unread/count")
    public ResponseEntity<ApiResponse<Long>> countUnread() {
        AuthenticatedUser user = authenticatedUser();

        return ResponseEntity.ok(ResponseUtil.success(
                notificationService.countUnread(user.idResident()),
                "Contagem retornada com sucesso!"));
    }

    // Marca uma notificação como lida
    @PatchMapping("/{id}/seen")
    public ResponseEntity<ApiResponse<NotificationResponse>> markAsSeen(@PathVariable Long id) {
        AuthenticatedUser user = authenticatedUser();

        return ResponseEntity.ok(ResponseUtil.success(
                notificationService.markAsSeen(id, user.idResident()),
                "Notificação marcada como lida!"));
    }

    // Marca todas como lidas
    @PatchMapping("/seen/all")
    public ResponseEntity<ApiResponse<Void>> markAllAsSeen() {
        AuthenticatedUser user = authenticatedUser();
        notificationService.markAllAsSeen(user.idResident());

        return ResponseEntity.ok(ResponseUtil.success(null,
                "Todas as notificações marcadas como lidas!"));
    }

    private AuthenticatedUser authenticatedUser() {
        return (AuthenticatedUser) SecurityContextHolder
                .getContext().getAuthentication().getPrincipal();
    }
}