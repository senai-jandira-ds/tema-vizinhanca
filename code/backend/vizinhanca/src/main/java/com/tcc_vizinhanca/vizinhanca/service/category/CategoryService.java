/***************************************************
 * Objetivo: Serviço responsável pelas regras de negócio relacionadas
 * à entidade Category, incluindo operações de consulta e validações
 * Data: 15/05/2026
 * Autor: Leonardo Scotti
 * Versão: 1.0.05.26
 * *************************************************/

package com.tcc_vizinhanca.vizinhanca.service.category;

import com.tcc_vizinhanca.vizinhanca.entity.category.Category;
import com.tcc_vizinhanca.vizinhanca.repository.category.CategoryRepository;
import lombok.NonNull;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import java.util.List;

@Service
public class CategoryService {

    @Autowired
    private CategoryRepository categoryRepository;

    // SELECT ALL
    public List<Category> getSelectAllCategories() {
        return categoryRepository.findAll();
    }

    // SELECT BY ID
    public Category getSelectCategoryById(@NonNull Long id) {
        return categoryRepository.findById(id)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Categoria não encontrada!"));
    }

    // INSERT CATEGORY
    public Category setInsertCategory(@NonNull Category category) {
        category.setId(null);

        return categoryRepository.save(category);
    }

    // UPDATE CATEGORY
    public Category setUpdateCategory(@NonNull Category category, Long idCategory) {
        Category existingCategory = getSelectCategoryById(idCategory);

        BeanUtils.copyProperties(
                category, existingCategory, "id"
        );

        return categoryRepository.save(existingCategory);
    }

    // DELETE CONDOMINIUM
    public void setDeleteCategory(Long idCategory) {
        if (!categoryRepository.existsById(idCategory))
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Categoria não encontrada!");

        categoryRepository.deleteById(idCategory);
    }
}