package com.tcc_vizinhanca.vizinhanca.service.condominium;

import com.tcc_vizinhanca.vizinhanca.entity.condominium.CondominiumAddress;
import com.tcc_vizinhanca.vizinhanca.repository.condominium.CondominiumAddressRepository;
import lombok.NonNull;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import java.util.List;

@Service
public class CondominiumAddressService {

    @Autowired
    private CondominiumAddressRepository condominiumAddressRepository;

    // SELECT ALL
    public List<CondominiumAddress> getSelectAllCondominiumsAddress() {
        return condominiumAddressRepository.findAll();
    }

    // SELECT BY ID
    public CondominiumAddress getSelectCondominiumAddressById(Long id) {
        CondominiumAddress condominiumAddress = condominiumAddressRepository.findById(id)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Endereço não encontrado no banco de dados!"));

        return condominiumAddress;
    }

    // INSERT CONDOMINIUM ADDRES
    public CondominiumAddress setInsertCondominiumAddres(@NonNull CondominiumAddress condominiumAddress) {
        condominiumAddress.setId(null);

        return condominiumAddressRepository.save(condominiumAddress);
    }

    // UPDATE CONDOMINIUM
    public CondominiumAddress setUpdateCondominiumAddress(@NonNull CondominiumAddress condominiumAddress, Long id) {
        CondominiumAddress existingCondominiumAddress = getSelectCondominiumAddressById(id);

        BeanUtils.copyProperties(condominiumAddress, existingCondominiumAddress, "id");

        return condominiumAddressRepository.save(existingCondominiumAddress);

    }

    // DELETE CONDOMINIUM ADDRESS
    public void setDeleteCondominiumAddressById(Long id) {
        if(!condominiumAddressRepository.existsById(id)) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Endereço não encontrado no banco de dados!");
        }

        condominiumAddressRepository.deleteById(id);
    }

}
