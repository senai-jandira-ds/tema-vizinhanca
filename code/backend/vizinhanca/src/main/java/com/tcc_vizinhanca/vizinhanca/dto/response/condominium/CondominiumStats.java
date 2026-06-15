package com.tcc_vizinhanca.vizinhanca.dto.response.condominium;

public record CondominiumStats(
        Long amount_residents,
        Long amount_services,
        Long amount_objects,
        Long amount_reports
) {}
