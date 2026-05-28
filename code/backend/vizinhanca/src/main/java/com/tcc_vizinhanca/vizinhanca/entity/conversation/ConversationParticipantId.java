package com.tcc_vizinhanca.vizinhanca.entity.conversation;

import jakarta.persistence.*;
import lombok.*;

import java.io.Serializable;

@Embeddable
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode
public class ConversationParticipantId implements Serializable {

    private Long conversationId;
    private Long moradorId;

}
