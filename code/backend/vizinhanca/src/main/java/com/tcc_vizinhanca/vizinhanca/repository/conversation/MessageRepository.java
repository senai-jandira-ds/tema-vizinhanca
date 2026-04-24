package com.tcc_vizinhanca.vizinhanca.repository.conversation;

import com.tcc_vizinhanca.vizinhanca.entity.conversation.Message;
import org.springframework.data.jpa.repository.JpaRepository;

public interface MessageRepository extends JpaRepository<Message, Long> {
}
