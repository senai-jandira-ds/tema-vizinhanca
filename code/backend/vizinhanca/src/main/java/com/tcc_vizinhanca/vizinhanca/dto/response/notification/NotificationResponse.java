/***************************************************
 * Objetivo: DTO de resposta para notificações
 * Data: 27/05/2026
 * Autor: Leonardo Scotti
 * Versão: 1.0.05.26
 * *************************************************/

package com.tcc_vizinhanca.vizinhanca.dto.response.notification;

import com.tcc_vizinhanca.vizinhanca.entity.notification.Notification;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDate;

@Getter
@Setter
@NoArgsConstructor
public class NotificationResponse {

    private Long id;
    private String title;
    private LocalDate creationDate;
    private Boolean seen;
    private String originType;
    private Long originId;
    private Long residentId;

    public NotificationResponse(Notification notification) {
        this.id           = notification.getId();
        this.title        = notification.getTitle();
        this.creationDate = notification.getCreationDate();
        this.seen         = notification.getSeen();
        this.originType   = notification.getOriginType();
        this.originId     = notification.getOriginId();
        this.residentId   = notification.getResident().getId();
    }
}