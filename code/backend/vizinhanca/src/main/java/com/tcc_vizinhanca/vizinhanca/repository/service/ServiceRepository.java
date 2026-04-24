package com.tcc_vizinhanca.vizinhanca.repository.service;

import com.tcc_vizinhanca.vizinhanca.entity.service.Service;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ServiceRepository extends JpaRepository<Service, Long> {
}
