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

        this.key = Keys.hmacShaKeyFor(
                SECRET.getBytes()
        );
    }

    public String gerarToken(
            String username,
            Long idCondominium,
            Long idResident,
            String typePerfil
    ) {

        return Jwts.builder()
                .setSubject(username)
                .claim("id_condominium", idCondominium)
                .claim("id_morador", idResident)
                .claim("tipo_perfil", typePerfil)
                .setIssuedAt(new Date())
                .setExpiration(
                        new Date(
                                System.currentTimeMillis()
                                        + 86400000
                        )
                )
                .signWith(key, SignatureAlgorithm.HS256)
                .compact();
    }

    public Claims extrairClaims(String token) {

        return Jwts.parserBuilder()
                .setSigningKey(key)
                .build()
                .parseClaimsJws(token)
                .getBody();
    }

    public boolean validarToken(String token) {

        try {

            extrairClaims(token);
            return true;

        } catch (Exception ex) {

            return false;
        }
    }
}