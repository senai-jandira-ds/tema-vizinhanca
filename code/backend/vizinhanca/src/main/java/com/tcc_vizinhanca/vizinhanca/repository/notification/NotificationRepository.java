package com.tcc_vizinhanca.vizinhanca.repository.notification;

import com.tcc_vizinhanca.vizinhanca.entity.notification.Notification;
import org.springframework.data.jpa.repository.JpaRepository;

public interface NotificationRepository extends JpaRepository<Notification, Long> {
}
