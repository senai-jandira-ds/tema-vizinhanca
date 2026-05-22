package com.tcc_vizinhanca.vizinhanca.entity.conversation;

import com.tcc_vizinhanca.vizinhanca.entity.resident.Resident;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Table(name = "tbl_conversa_participante")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class ConversationParticipant {

    @EmbeddedId
    private ConversationParticipantId id;

    @ManyToOne
    @MapsId("conversationId")
    @JoinColumn(name = "id_conversa")
    private Conversation conversation;

    @ManyToOne
    @MapsId("moradorId")
    @JoinColumn(name = "id_morador")
    private Resident resident;
}
