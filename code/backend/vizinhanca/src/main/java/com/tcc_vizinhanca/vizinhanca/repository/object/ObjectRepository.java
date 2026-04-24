package com.tcc_vizinhanca.vizinhanca.repository.object;

import com.tcc_vizinhanca.vizinhanca.entity.object.Object;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ObjectRepository extends JpaRepository<Object, Long> {
}
