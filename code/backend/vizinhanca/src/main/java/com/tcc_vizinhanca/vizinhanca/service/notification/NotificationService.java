package com.tcc_vizinhanca.vizinhanca.service.notification;

import com.tcc_vizinhanca.vizinhanca.dto.response.notification.NotificationResponse;
import com.tcc_vizinhanca.vizinhanca.entity.notification.Notification;
import com.tcc_vizinhanca.vizinhanca.entity.resident.Resident;
import com.tcc_vizinhanca.vizinhanca.repository.notification.NotificationRepository;
import com.tcc_vizinhanca.vizinhanca.repository.resident.ResidentRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import java.time.LocalDate;
import java.util.List;

@Service
public class NotificationService {
    @Autowired
    private NotificationRepository notificationRepository;

    @Autowired
    private ResidentRepository residentRepository;

    @Autowired
    private SimpMessagingTemplate messagingTemplate;

    /**
     * Cria e entrega uma notificação para um morador específico.
     * Persiste no banco (garante entrega mesmo offline)
     * e faz push via WebSocket se o usuário estiver conectado.
     *
     * @param residentId  ID do morador destinatário
     * @param title       Título da notificação
     * @param originType  Tipo da origem: "SERVICE", "OBJECT", "PUBLICATION", "REPORT"
     * @param originId    ID do registro de origem
     */
    @Async
    public void notify(Long residentId, String title, String originType, Long originId) {
        Resident resident = residentRepository.findById(residentId)
                .orElseThrow(() -> new ResponseStatusException(
                        HttpStatus.NOT_FOUND, "Morador não encontrado!"));

        Notification notification = new Notification();
        notification.setTitle(title);
        notification.setCreationDate(LocalDate.now());
        notification.setSeen(false);
        notification.setOriginType(originType);
        notification.setOriginId(originId);
        notification.setResident(resident);

        Notification saved = notificationRepository.save(notification);

        // Push WebSocket — entregue apenas se o usuário estiver conectado
        // Se offline, ele busca via GET /notification no próximo acesso
        messagingTemplate.convertAndSendToUser(
                resident.getEmail(),
                "/queue/notifications",
                new NotificationResponse(saved)
        );
    }

    /**
     * Notifica todos os moradores de um condomínio.
     * Útil para publicações e avisos gerais.
     */
    @Async
    public void notifyAllResidents(List<Long> residentIds, String title,
                                   String originType, Long originId) {
        residentIds.forEach(id -> notify(id, title, originType, originId));
    }

    // Busca todas as notificações de um morador ordenadas pela mais recente
    public List<NotificationResponse> getByResidentId(Long residentId) {
        return notificationRepository
                .findByResidentIdOrderByCreationDateDesc(residentId)
                .stream()
                .map(NotificationResponse::new)
                .toList();
    }

    // Busca apenas as não lidas
    public List<NotificationResponse> getUnreadByResidentId(Long residentId) {
        return notificationRepository
                .findByResidentIdAndSeenFalseOrderByCreationDateDesc(residentId)
                .stream()
                .map(NotificationResponse::new)
                .toList();
    }

    // Conta notificações não lidas — útil para badge no frontend
    public Long countUnread(Long residentId) {
        return notificationRepository.countByResidentIdAndSeenFalse(residentId);
    }

    // Marca uma notificação como lida
    public NotificationResponse markAsSeen(Long notificationId, Long residentId) {
        Notification notification = notificationRepository.findById(notificationId)
                .orElseThrow(() -> new ResponseStatusException(
                        HttpStatus.NOT_FOUND, "Notificação não encontrada!"));

        if (!notification.getResident().getId().equals(residentId)) {
            throw new ResponseStatusException(HttpStatus.FORBIDDEN,
                    "Você não tem permissão para acessar esta notificação!");
        }

        notification.setSeen(true);
        return new NotificationResponse(notificationRepository.save(notification));
    }

    // Marca todas as notificações do morador como lidas
    public void markAllAsSeen(Long residentId) {
        List<Notification> unread = notificationRepository
                .findByResidentIdAndSeenFalseOrderByCreationDateDesc(residentId);

        unread.forEach(n -> n.setSeen(true));
        notificationRepository.saveAll(unread);
    }

}
