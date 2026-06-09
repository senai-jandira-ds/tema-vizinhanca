/***************************************************
 * Objetivo: Serviço responsável pelas regras de negócio relacionadas
 * à entidade Object, incluindo operações de CRUD, upload de imagem
 * e validações
 * Data: 26/05/2026
 * Autor: Leonardo Scotti
 * Versão: 1.0.05.26
 * *************************************************/

package com.tcc_vizinhanca.vizinhanca.service.object;

import com.tcc_vizinhanca.vizinhanca.entity.category.Category;
import com.tcc_vizinhanca.vizinhanca.entity.condominium.Condominium;
import com.tcc_vizinhanca.vizinhanca.entity.object.Object;
import com.tcc_vizinhanca.vizinhanca.entity.resident.Resident;
import com.tcc_vizinhanca.vizinhanca.repository.object.ObjectRepository;
import com.tcc_vizinhanca.vizinhanca.service.category.CategoryService;
import com.tcc_vizinhanca.vizinhanca.service.condominium.ActivityViewService;
import com.tcc_vizinhanca.vizinhanca.service.condominium.CondominiumService;
import com.tcc_vizinhanca.vizinhanca.service.resident.ResidentService;
import com.tcc_vizinhanca.vizinhanca.service.storage.BlobStorageService;
import com.tcc_vizinhanca.vizinhanca.specification.object.ObjectSpecification;
import lombok.NonNull;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.server.ResponseStatusException;

import java.util.List;

@Service
public class ObjectService {

    @Autowired
    private ObjectRepository objectRepository;

    @Autowired
    private ResidentService residentService;

    @Autowired
    private CondominiumService condominiumService;

    @Autowired
    private CategoryService categoryService;

    @Autowired
    private BlobStorageService blobStorageService;

    @Autowired
    private ActivityViewService activityViewService;

    // SELECT ALL BY CONDOMINIUM
    public Page<Object> getSelectAllObjectsByCondominiumId(Long condominiumId, Pageable pageable) {
        return objectRepository.findByCondominiumId(condominiumId, pageable);
    }

    // SELECT BY RESIDENT
    public Page<Object> getSelectObjectsByResidentId(Long residentId, Pageable pageable) {
        return objectRepository.findByResidentId(residentId, pageable);
    }

    // SELECT BY STATUS
    public Page<Object> getSelectObjectsByStatus(Long condominiumId, String status, Pageable pageable) {
        return objectRepository.findByCondominiumIdAndStatus(condominiumId, status, pageable);
    }

    // SELECT BY CATEGORY
    public Page<Object> getSelectObjectsByCategory(Long condominiumId, Long categoryId, Pageable pageable) {
        return objectRepository.findByCondominiumIdAndCategoryId(condominiumId, categoryId, pageable);
    }

    // SELECT WITH FILTERS
    public Page<Object> getSelectObjectsByFilters(
            Long condominiumId,
            List<String> statuses,
            List<Long> categoryIds,
            List<Long> blockIds,
            Pageable pageable) {

        Specification<Object> spec = Specification.where(ObjectSpecification.hasCondominium(condominiumId));

        if (statuses != null && !statuses.isEmpty()) {
            spec = spec.and(ObjectSpecification.hasStatuses(statuses));
        }

        if (categoryIds != null && !categoryIds.isEmpty()) {
            spec = spec.and(ObjectSpecification.hasCategories(categoryIds));
        }

        if (blockIds != null && !blockIds.isEmpty()) {
            spec = spec.and(ObjectSpecification.hasBlocks(blockIds));
        }

        return objectRepository.findAll(spec, pageable);
    }

    // SELECT WITH FILTERS
    public Page<Object> getSelectResidentObjectsByFilters(
            Long residentId,
            List<String> statuses,
            List<Long> categoryIds,
            Pageable pageable) {

        Specification<Object> spec = Specification.where(ObjectSpecification.hasResident(residentId));

        if (statuses != null && !statuses.isEmpty()) {
            spec = spec.and(ObjectSpecification.hasStatuses(statuses));
        }

        if (categoryIds != null && !categoryIds.isEmpty()) {
            spec = spec.and(ObjectSpecification.hasCategories(categoryIds));
        }

        return objectRepository.findAll(spec, pageable);
    }

    // SELECT BY ID
    public Object getSelectObjectById(@NonNull Long id) {
        System.out.println(id);
        System.out.println(objectRepository.findById(id));
        return objectRepository.findById(id)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Objeto não encontrado!"));
    }

    // INSERT
    @CacheEvict(value = "activities", key = "#idCondominium")
    public Object setInsertObject(
            @NonNull Object object,
            MultipartFile photo,
            Long residentId,
            Long condominiumId,
            Long categoryId) {

        Resident resident = residentService.getSelectResidentById(residentId);
        Condominium condominium = condominiumService.getSelectCondominiumById(condominiumId);
        Category category = categoryService.getSelectCategoryById(categoryId);

        if (photo != null && !photo.isEmpty()) {
            String photoUrl = blobStorageService.uploadFile(photo, "objects");
            object.setPhoto(photoUrl);
        }

        object.setId(null);
        object.setResident(resident);
        object.setCondominium(condominium);
        object.setCategory(category);

        return objectRepository.save(object);
    }

    // UPDATE
    public Object setUpdateObject(
            @NonNull Long id,
            Object updatedObject,
            MultipartFile photo,
            Long categoryId) {

        Object existingObject = getSelectObjectById(id);
        Long condominiumId = existingObject.getCondominium().getId();

        if (photo != null && !photo.isEmpty()) {
            if (existingObject.getPhoto() != null) {
                blobStorageService.deleteFile(existingObject.getPhoto());
            }
            String photoUrl = blobStorageService.uploadFile(photo, "objects");
            existingObject.setPhoto(photoUrl);
        }

        if (updatedObject.getTitle() != null) existingObject.setTitle(updatedObject.getTitle());
        if (updatedObject.getDeadline() != null) existingObject.setDeadline(updatedObject.getDeadline());
        if (updatedObject.getDescription() != null) existingObject.setDescription(updatedObject.getDescription());
        if (updatedObject.getStatus() != null) existingObject.setStatus(updatedObject.getStatus());

        if (categoryId != null) {
            Category category = categoryService.getSelectCategoryById(categoryId);
            existingObject.setCategory(category);
        }

        Object saved = objectRepository.save(existingObject);
        activityViewService.evictCache(condominiumId);
        return saved;
    }

    // DELETE
    @CacheEvict(value = "activities", key = "#idCondominium")
    public void setDeleteObjectById(@NonNull Long id) {
        Object existing = getSelectObjectById(id);

        if (existing.getPhoto() != null) {
            try {
                blobStorageService.deleteFile(existing.getPhoto());
            } catch (Exception ignored) {
                /* TODO */
            }
        }

        objectRepository.deleteById(id);
    }
}