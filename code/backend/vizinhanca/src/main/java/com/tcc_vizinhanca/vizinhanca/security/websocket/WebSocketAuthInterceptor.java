/***************************************************
 * Objetivo: Interceptor responsável por autenticar conexões WebSocket
 * via token JWT enviado no header da conexão STOMP
 * Data: 27/05/2026
 * Autor: Leonardo Scotti
 * Versão: 1.0.05.26
 * *************************************************/

package com.tcc_vizinhanca.vizinhanca.security.websocket;

import com.tcc_vizinhanca.vizinhanca.security.jwt.AuthenticatedUser;
import com.tcc_vizinhanca.vizinhanca.security.jwt.JwtService;
import io.jsonwebtoken.Claims;
import org.jetbrains.annotations.NotNull;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.messaging.Message;
import org.springframework.messaging.MessageChannel;
import org.springframework.messaging.simp.stomp.StompCommand;
import org.springframework.messaging.simp.stomp.StompHeaderAccessor;
import org.springframework.messaging.support.ChannelInterceptor;
import org.springframework.messaging.support.MessageHeaderAccessor;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
public class WebSocketAuthInterceptor implements ChannelInterceptor {

    @Autowired
    private JwtService jwtService;

    @Override
    public Message<?> preSend(@NotNull Message<?> message, @NotNull MessageChannel channel) {
        StompHeaderAccessor accessor =
                MessageHeaderAccessor.getAccessor(message, StompHeaderAccessor.class);

        if (accessor == null) return message;

        if (StompCommand.CONNECT.equals(accessor.getCommand())) {
            String authHeader = accessor.getFirstNativeHeader("Authorization");

            if (authHeader == null || !authHeader.startsWith("Bearer ")) {
                throw new IllegalArgumentException("Token ausente ou inválido na conexão WebSocket");
            }

            String token = authHeader.substring(7);

            if (!jwtService.validarToken(token)) {
                throw new IllegalArgumentException("Token JWT inválido");
            }

            Claims claims     = jwtService.extrairClaims(token);
            String email      = claims.getSubject();
            String typePerfil = claims.get("type_perfil", String.class);
            Number idCondRaw  = (Number) claims.get("id_condominium");
            Number idResRaw   = (Number) claims.get("id_resident");

            Long idCondominium = idCondRaw != null ? idCondRaw.longValue() : null;
            Long idResident    = idResRaw  != null ? idResRaw.longValue()  : null;

            AuthenticatedUser authenticatedUser =
                    new AuthenticatedUser(email, typePerfil, idCondominium, idResident);

            UsernamePasswordAuthenticationToken auth =
                    new UsernamePasswordAuthenticationToken(
                            authenticatedUser,
                            null,
                            List.of(new SimpleGrantedAuthority("ROLE_" + typePerfil))
                    );

            accessor.setUser(auth);
        }

        return message;
    }
}