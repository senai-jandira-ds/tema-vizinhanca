/***************************************************
* Objetivo: Arquivo responsável por rodar a API
* Data: 22/04/2026
* Autor: Leonardo Scotti
* Versão: 1.0.04.26
* *************************************************/

package com.tcc_vizinhanca.vizinhanca;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;

@SpringBootApplication
public class VizinhancaApplication {

    public static void main(String[] args) {
        SpringApplication.run(VizinhancaApplication.class, args);

        PasswordEncoder passwordEncoder = new BCryptPasswordEncoder();
        System.out.println(passwordEncoder.encode("senha88"));
    }

}
