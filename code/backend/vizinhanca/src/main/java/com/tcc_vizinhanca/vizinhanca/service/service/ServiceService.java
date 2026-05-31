/***************************************************
 * Objetivo: Serviço responsável pelas regras de negócio relacionadas
 * à entidade Service, incluindo operações de CRUD e validações
 * Data: 15/05/2026
 * Autor: Leonardo Scotti
 * Versão: 1.0.05.26
 * *************************************************/

package com.tcc_vizinhanca.vizinhanca.service.service;

import com.tcc_vizinhanca.vizinhanca.entity.category.Category;
import com.tcc_vizinhanca.vizinhanca.entity.condominium.Condominium;
import com.tcc_vizinhanca.vizinhanca.entity.resident.Resident;
import com.tcc_vizinhanca.vizinhanca.entity.service.Service;
import com.tcc_vizinhanca.vizinhanca.repository.service.ServiceRepository;
import com.tcc_vizinhanca.vizinhanca.service.category.CategoryService;
import com.tcc_vizinhanca.vizinhanca.service.condominium.CondominiumService;
import com.tcc_vizinhanca.vizinhanca.service.resident.ResidentService;
import com.tcc_vizinhanca.vizinhanca.specification.service.ServiceSpecification;
import lombok.NonNull;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Component;
import org.springframework.web.server.ResponseStatusException;

import java.util.List;

@Component
public class ServiceService {

    @Autowired
    private ServiceRepository serviceRepository;

    @Autowired
    private ResidentService residentService;

    @Autowired
    private CondominiumService condominiumService;

    @Autowired
    private CategoryService categoryService;

    // SELECT ALL BY CONDOMINIUM
    public Page<Service> getSelectAllServicesByCondominiumId(Long condominiumId, Pageable pageable) {
        return serviceRepository.findByCondominiumId(condominiumId, pageable);
    }

    // SELECT BY RESIDENT
    public Page<Service> getSelectServicesByResidentId(Long residentId, Pageable pageable) {
        return serviceRepository.findByResidentId(residentId, pageable);
    }

    // SELECT BY STATUS
    public Page<Service> getSelectServicesByStatus(Long condominiumId, String status, Pageable pageable) {
        return serviceRepository.findByCondominiumIdAndStatus(condominiumId, status, pageable);
    }

    // SELECT BY CATEGORY
    public Page<Service> getSelectServicesByCategory(Long condominiumId, Long categoryId, Pageable pageable) {
        return serviceRepository.findByCondominiumIdAndCategoryId(condominiumId, categoryId, pageable);
    }

    // SELECT WITH FILTERS
    public Page<Service> getSelectServicesByFilters(
            Long condominiumId,
            List<String> statuses,
            List<Long> categoryIds,
            List<Long> blockIds,
            Pageable pageable) {

        Specification<Service> spec = Specification
                .where(ServiceSpecification.hasCondominium(condominiumId));

        if (statuses != null && !statuses.isEmpty()) {
            spec = spec.and(ServiceSpecification.hasStatuses(statuses));
        }

        if (categoryIds != null && !categoryIds.isEmpty()) {
            spec = spec.and(ServiceSpecification.hasCategories(categoryIds));
        }

        if (blockIds != null && !blockIds.isEmpty()) {
            spec = spec.and(ServiceSpecification.hasBlocks(blockIds));
        }

        return serviceRepository.findAll(spec, pageable);
    }

    // SELECT WITH FILTERS
    public Page<Service> getSelectResidentServicesByFilters(
            Long residentId,
            List<String> statuses,
            List<Long> categoryIds,
            Pageable pageable) {

        Specification<Service> spec = Specification
                .where(ServiceSpecification.hasResident(residentId));

        if (statuses != null && !statuses.isEmpty()) {
            spec = spec.and(ServiceSpecification.hasStatuses(statuses));
        }

        if (categoryIds != null && !categoryIds.isEmpty()) {
            spec = spec.and(ServiceSpecification.hasCategories(categoryIds));
        }

        return serviceRepository.findAll(spec, pageable);
    }

    // SELECT BY ID
    public Service getSelectServiceById(@NonNull Long id) {
        return serviceRepository.findById(id)
                .orElseThrow(() -> new ResponseStatusException(
                        HttpStatus.NOT_FOUND, "Serviço não encontrado!"));
    }

    // INSERT
    public Service setInsertService(@NonNull Service service, Long residentId, Long condominiumId, Long categoryId) {
        Resident resident = residentService.getSelectResidentById(residentId);
        Condominium condominium = condominiumService.getSelectCondominiumById(condominiumId);
        Category category = categoryService.getSelectCategoryById(categoryId);

        System.out.println(residentService.getSelectResidentById(residentId));
        System.out.println(condominiumService.getSelectCondominiumById(condominiumId));
        System.out.println(categoryService.getSelectCategoryById(categoryId));

        service.setResident(resident);
        service.setCondominium(condominium);
        service.setCategory(category);

        return serviceRepository.save(service);
    }

    // UPDATE
    public Service setUpdateService(@NonNull Long id, Service updatedService, Long categoryId) {
        Service existingService = getSelectServiceById(id);

        if (updatedService.getPhoto() != null) existingService.setPhoto(updatedService.getPhoto());
        if (updatedService.getTitle() != null) existingService.setTitle(updatedService.getTitle());
        if (updatedService.getEstimatedTime() != null) existingService.setEstimatedTime(updatedService.getEstimatedTime());
        if (updatedService.getUrgency() != null) existingService.setUrgency(updatedService.getUrgency());
        if (updatedService.getDescription() != null) existingService.setDescription(updatedService.getDescription());
        if (updatedService.getStatus() != null) existingService.setStatus(updatedService.getStatus());

        if (categoryId != null) {
            Category category = categoryService.getSelectCategoryById(categoryId);
            existingService.setCategory(category);
        }

        return serviceRepository.save(existingService);
    }

    // DELETE
    public void setDeleteServiceById(@NonNull Long id) {
        if (!serviceRepository.existsById(id)) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Serviço não encontrado!");
        }
        serviceRepository.deleteById(id);
    }
}