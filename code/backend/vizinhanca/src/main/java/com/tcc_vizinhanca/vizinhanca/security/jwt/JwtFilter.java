/************************************************************
* Objetivo: Validar token JWT e autenticar requisições na API
* Data: 22/04/2026
* Autor: Leonardo Scotti
* Versão: 1.0.04.26
* **********************************************************/

package com.tcc_vizinhanca.vizinhanca.security.jwt;

import com.tcc_vizinhanca.vizinhanca.util.ResponseUtil;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;

@Component
public class JwtFilter extends OncePerRequestFilter {

    private final JwtService jwtService;

    @Autowired
    private CustomUserDetailsService customUserDetailsService;

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

        if (!jwtService.validarToken(token)) {
            response.sendError(ResponseUtil.error(401, "Token ausente ou inválido").getStatusCode());
            return;
        }

        String username = jwtService.extrairUsername(token);

        System.out.println("TOKEN VÁLIDO: " + jwtService.validarToken(token));
        System.out.println("USERNAME: " + jwtService.extrairUsername(token));

        UserDetails userDetails =
                customUserDetailsService.loadUserByUsername(username);

        UsernamePasswordAuthenticationToken auth =
                UsernamePasswordAuthenticationToken.authenticated(
                        userDetails,
                        null,
                        userDetails.getAuthorities()
                );

        auth.setDetails(userDetails);

        SecurityContextHolder.getContext().setAuthentication(auth);

        System.out.println("AUTENTICAÇÃO SETADA: " +
                SecurityContextHolder.getContext().getAuthentication());

        filterChain.doFilter(request, response);
    }
}
