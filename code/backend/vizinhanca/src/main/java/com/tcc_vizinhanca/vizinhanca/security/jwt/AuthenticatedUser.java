package com.tcc_vizinhanca.vizinhanca.security.jwt;

public record AuthenticatedUser(
        String email,
        String typePerfil,
        Long idCondominium,
        Long idResident
) {
}