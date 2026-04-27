/***************************************************
 * Objetivo: Entidade responsável por representar as mensagens enviadas nas conversas,
 * armazenando conteúdo textual, data de envio, possíveis mídias e o relacionamento
 * com a conversa e o autor da mensagem
 * Data: 24/04/2026
 * Autor: Leonardo Scotti
 * Versão: 1.0.04.26
 * *************************************************/

package com.tcc_vizinhanca.vizinhanca.entity.conversation;

import com.tcc_vizinhanca.vizinhanca.entity.resident.Resident;
import com.tcc_vizinhanca.vizinhanca.enums.Status;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDateTime;

@Entity
@Table(name = "tbl_mensagem")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class Message {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id_mensagem")
    private Long id;

    @Column(name = "texto", columnDefinition = "TEXT", nullable = false)
    private String text;

    @Column(name = "data_criacao", nullable = false)
    private LocalDateTime createdDate;

    @Enumerated(EnumType.STRING)
    private Status status;

    @ManyToOne
    @JoinColumn(name = "id_conversa", nullable = false)
    private Conversation conversation;

    @ManyToOne
    @JoinColumn(name = "id_resident", nullable = false)
    private Resident resident;

    @PrePersist
    public void prePersist(){
        this.createdDate = LocalDateTime.now();
        this.status = Status.SENT;
    }

}
