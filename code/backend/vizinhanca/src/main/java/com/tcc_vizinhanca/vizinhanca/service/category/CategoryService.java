/***************************************************
 * Objetivo: Serviço responsável pelas regras de negócio relacionadas
 * à entidade Category, incluindo operações de consulta e validações
 * Data: 26/05/2026
 * Autor: Leonardo Scotti
 * Versão: 1.0.05.26
 * *************************************************/

package com.tcc_vizinhanca.vizinhanca.service.category;

import com.tcc_vizinhanca.vizinhanca.entity.category.Category;
import com.tcc_vizinhanca.vizinhanca.entity.category.TypeCategory;
import com.tcc_vizinhanca.vizinhanca.repository.category.CategoryRepository;
import com.tcc_vizinhanca.vizinhanca.repository.category.TypeCategoryRepository;
import lombok.NonNull;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import java.util.List;

@Service
public class CategoryService {

    @Autowired
    private CategoryRepository categoryRepository;

    @Autowired
    private TypeCategoryRepository typeCategoryRepository;

    // SELECT ALL
    public List<Category> getSelectAllCategories() {
        return categoryRepository.findAll();
    }

    // SELECT BY TYPE
    public List<Category> getSelectCategoriesByTypeId(@NonNull Long typeCategoryId) {
        if (!typeCategoryRepository.existsById(typeCategoryId))
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Tipo de categoria não encontrado!");

        return categoryRepository.findByTypeCategoryId(typeCategoryId);
    }

    // SELECT BY ID
    public Category getSelectCategoryById(@NonNull Long id) {
        return categoryRepository.findById(id)
                .orElseThrow(() -> new ResponseStatusException(
                        HttpStatus.NOT_FOUND, "Categoria não encontrada!"));
    }

    // SELECT BY CONDOMINIUM
    public List<Category> getSelectCategoriesByCondominiumId(Long condominiumId) {
        return categoryRepository.findByCondominiumId(condominiumId);
    }

    // INSERT CATEGORY
    public Category setInsertCategory(@NonNull Category category, Long typeCategoryId) {
        TypeCategory typeCategory = typeCategoryRepository.findById(typeCategoryId)
                .orElseThrow(() -> new ResponseStatusException(
                        HttpStatus.NOT_FOUND, "Tipo de categoria não encontrado!"));

        category.setId(null);
        category.setTypeCategory(typeCategory);

        return categoryRepository.save(category);
    }

    // UPDATE CATEGORY
    public Category setUpdateCategory(@NonNull Category category, Long idCategory, Long typeCategoryId) {
        Category existingCategory = getSelectCategoryById(idCategory);

        if (category.getName() != null) existingCategory.setName(category.getName());
        if (category.getDescription() != null) existingCategory.setDescription(category.getDescription());

        if (typeCategoryId != null) {
            TypeCategory typeCategory = typeCategoryRepository.findById(typeCategoryId)
                    .orElseThrow(() -> new ResponseStatusException(
                            HttpStatus.NOT_FOUND, "Tipo de categoria não encontrado!"));
            existingCategory.setTypeCategory(typeCategory);
        }

        return categoryRepository.save(existingCategory);
    }

    // DELETE CATEGORY
    public void setDeleteCategory(Long idCategory) {
        if (!categoryRepository.existsById(idCategory))
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Categoria não encontrada!");

        categoryRepository.deleteById(idCategory);
    }
}