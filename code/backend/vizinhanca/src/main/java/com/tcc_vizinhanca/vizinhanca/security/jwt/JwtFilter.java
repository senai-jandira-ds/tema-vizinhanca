/************************************************************
* Objetivo: Validar token JWT e autenticar requisições na API
* Data: 22/04/2026
* Autor: Leonardo Scotti
* Versão: 1.0.04.26
* **********************************************************/

package com.tcc_vizinhanca.vizinhanca.security.jwt;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.security.authentication.AnonymousAuthenticationToken;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.authority.AuthorityUtils;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;
import java.util.Collections;

@Component
public class JwtFilter extends OncePerRequestFilter {

    private final JwtService jwtService;

    public JwtFilter(JwtService jwtService) {
        this.jwtService = jwtService;
    }

    @Override
    protected boolean shouldNotFilter(HttpServletRequest request) {
        String path = request.getRequestURI();
        String method = request.getMethod();

        return path.startsWith("/api/v1/auth")
                || path.startsWith("/h2-console")
                || (path.equals("/api/v1/condominium") && method.equals("POST"));
    }

    @Override
    protected void doFilterInternal(HttpServletRequest request,
                                    HttpServletResponse response,
                                    FilterChain filterChain)
            throws ServletException, IOException {

        String header = request.getHeader("Authorization");
        System.out.println("HEADER: " + header);

        if (header == null || !header.startsWith("Bearer ")) {
            filterChain.doFilter(request, response);
            return;
        }

        String token = header.substring(7);

        System.out.println("TOKEN VÁLIDO: " + jwtService.validarToken(token));
        System.out.println("USERNAME: " + jwtService.extrairUsername(token));

        if (!jwtService.validarToken(token)) {
            response.sendError(HttpServletResponse.SC_UNAUTHORIZED, "Token inválido");
            return;
        }

        String username = jwtService.extrairUsername(token);

        UsernamePasswordAuthenticationToken auth =
                new UsernamePasswordAuthenticationToken(
                        username,
                        null,
                        Collections.emptyList()
                );

        SecurityContextHolder.getContext().setAuthentication(auth);

        System.out.println("AUTENTICAÇÃO SETADA: " +
                SecurityContextHolder.getContext().getAuthentication());

        filterChain.doFilter(request, response);
    }
}
