package com.tcc_vizinhanca.vizinhanca.security.jwt;

import com.tcc_vizinhanca.vizinhanca.entity.condominium.Condominium;
import com.tcc_vizinhanca.vizinhanca.entity.resident.Resident;
import com.tcc_vizinhanca.vizinhanca.repository.condominium.CondominiumRepository;
import com.tcc_vizinhanca.vizinhanca.repository.resident.ResidentRepository;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class CustomUserDetailsService implements UserDetailsService {

    private final CondominiumRepository condominiumRepository;
    private final ResidentRepository residentRepository;

    public CustomUserDetailsService(CondominiumRepository condominiumRepository,
                                    ResidentRepository residentRepository) {

        this.condominiumRepository = condominiumRepository;
        this.residentRepository = residentRepository;
    }

    @Override
    @Cacheable(value = "userDetails", key = "#email")
    public UserDetails loadUserByUsername(String email) {

        return condominiumRepository.findByEmail(email)
                .map(condominium -> (UserDetails) buildCondominium(condominium))
                .orElseGet(() -> residentRepository.findByEmail(email)
                        .map(resident -> (UserDetails) buildResident(resident))
                        .orElseThrow(() -> new UsernameNotFoundException("Usuário não encontrado")));
    }

    private UserDetails buildCondominium(Condominium condominium) {
        return new User(
                condominium.getEmail(),
                condominium.getPassword(),
                List.of(new SimpleGrantedAuthority("ROLE_CONDOMINIO"))
        );
    }

    private UserDetails buildResident(Resident resident) {
        return new User(
                resident.getEmail(),
                resident.getPassword(),
                List.of(new SimpleGrantedAuthority("ROLE_RESIDENT"))
        );
    }
}
