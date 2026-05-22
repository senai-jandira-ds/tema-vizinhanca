/**************************************************************
 * Objetivo: Arquivo responsável por manipular os tokens do JWT
 * Data: 22/04/2026
 * Autor: Leonardo Scotti
 * Versão: 1.0.04.26
 * ***********************************************************/

package com.tcc_vizinhanca.vizinhanca.security.jwt;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import io.jsonwebtoken.security.Keys;
import jakarta.annotation.PostConstruct;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.security.Key;
import java.util.Date;

@Component
public class JwtService {

    @Value("${jwt.secret}")
    private String SECRET;

    private Key key;

    @PostConstruct
    public void init() {
        this.key = Keys.hmacShaKeyFor(SECRET.getBytes());
    }

    public String gerarToken(String username, Long idCondominio, String tipoPerfil) {
        return Jwts.builder()
                .setSubject(username)
                .claim("id_condominio", idCondominio)
                .claim("tipo_perfil", tipoPerfil)
                .setIssuedAt(new Date())
                .setExpiration(new Date(System.currentTimeMillis() + 86400000))
                .signWith(key, SignatureAlgorithm.HS256)
                .compact();
    }

    public Claims extrairClaims(String token) {
        try {
            return Jwts.parserBuilder()
                    .setSigningKey(key)
                    .build()
                    .parseClaimsJws(token)
                    .getBody();
        } catch (Exception e) {
            return null;
        }
    }

    public boolean validarToken(String token) {
        return extrairClaims(token) != null;
    }

    public String extrairUsername(String token) {
        Claims claims = extrairClaims(token);
        return claims != null ? claims.getSubject() : null;
    }

    public Long extrairIdCondominio(String token) {
        Claims claims = extrairClaims(token);
        if (claims == null) return null;
        Object value = claims.get("id_condominio");
        return value != null ? Long.valueOf(value.toString()) : null;
    }

    public String extrairTipoPerfil(String token) {
        Claims claims = extrairClaims(token);
        return claims != null ? claims.get("tipo_perfil", String.class) : null;
    }
}