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

    @Column(name = "texto", nullable = false, columnDefinition = "TEXT")
    private String text;

    @Column(name = "data_envio", nullable = false)
    private LocalDateTime sendDate;

    @Column(name = "url_foto", nullable = false, columnDefinition = "TEXT")
    private String photoUrl;

    @ManyToOne
    @JoinColumn(name = "id_conversa")
    private Conversation conversation;

    @ManyToOne
    @JoinColumn(name = "id_autor")
    private Resident author;
}
