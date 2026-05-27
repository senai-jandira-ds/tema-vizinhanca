/***************************************************
 * Objetivo: Controlador responsável por expor os endpoints
 * da entidade Category, gerenciando as requisições HTTP
 * de listagem, busca, criação, atualização e remoção.
 * TypeCategory é dado pré-moldado no banco — apenas leitura.
 * Data: 26/05/2026
 * Autor: Leonardo Scotti
 * Versão: 1.0.05.26
 * *************************************************/

package com.tcc_vizinhanca.vizinhanca.controller.category;

import com.tcc_vizinhanca.vizinhanca.dto.request.category.CategoryCreateRequest;
import com.tcc_vizinhanca.vizinhanca.dto.request.category.CategoryUpdateRequest;
import com.tcc_vizinhanca.vizinhanca.dto.response.ApiResponse;
import com.tcc_vizinhanca.vizinhanca.dto.response.category.CategoryDetailResponse;
import com.tcc_vizinhanca.vizinhanca.dto.response.category.CategoryResponse;
import com.tcc_vizinhanca.vizinhanca.dto.response.category.TypeCategoryResponse;
import com.tcc_vizinhanca.vizinhanca.entity.category.Category;
import com.tcc_vizinhanca.vizinhanca.entity.category.TypeCategory;
import com.tcc_vizinhanca.vizinhanca.repository.category.TypeCategoryRepository;
import com.tcc_vizinhanca.vizinhanca.service.category.CategoryService;
import com.tcc_vizinhanca.vizinhanca.util.ResponseUtil;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/v1/category")
@Tag(name = "Category", description = "Endpoints para gerenciamento das categorias.")
public class CategoryController {

    @Autowired
    private CategoryService categoryService;

    @Autowired
    private TypeCategoryRepository typeCategoryRepository;

    // GET ALL
    @GetMapping
    public ResponseEntity<ApiResponse<CategoryResponse>> listAllCategories() {
        List<Category> categories = categoryService.getSelectAllCategories();
        return ResponseEntity.ok(ResponseUtil.success(
                new CategoryResponse(categories),
                "Lista de categorias retornada com sucesso!"));
    }

    // GET BY TYPE
    @GetMapping("/type/{typeId}/categories")
    public ResponseEntity<ApiResponse<CategoryResponse>> listCategoriesByType(@PathVariable Long typeId) {
        List<Category> categories = categoryService.getSelectCategoriesByTypeId(typeId);
        return ResponseEntity.ok(ResponseUtil.success(
                new CategoryResponse(categories),
                "Categorias do tipo retornadas com sucesso!"));
    }

    // GET BY ID
    @GetMapping("/{id}")
    public ResponseEntity<ApiResponse<CategoryDetailResponse>> searchCategoryById(@PathVariable Long id) {
        Category category = categoryService.getSelectCategoryById(id);
        return ResponseEntity.ok(ResponseUtil.success(
                new CategoryDetailResponse(category),
                "Categoria encontrada com sucesso!"));
    }

    // POST
    @PostMapping
    public ResponseEntity<ApiResponse<CategoryDetailResponse>> insertCategory(
            @Valid @RequestBody CategoryCreateRequest request) {

        Category category = new Category();
        category.setName(request.getName());
        category.setDescription(request.getDescription());

        Category saved = categoryService.setInsertCategory(category, request.getTypeCategoryId());

        return ResponseEntity.status(HttpStatus.CREATED)
                .body(ResponseUtil.success(
                        new CategoryDetailResponse(saved),
                        "Categoria criada com sucesso!"));
    }

    // PUT
    @PutMapping("/{id}")
    public ResponseEntity<ApiResponse<CategoryDetailResponse>> updateCategory(
            @PathVariable Long id,
            @Valid @RequestBody CategoryUpdateRequest request) {

        Category category = new Category();
        category.setName(request.getName());
        category.setDescription(request.getDescription());

        Category updated = categoryService.setUpdateCategory(category, id, request.getTypeCategoryId());

        return ResponseEntity.ok(ResponseUtil.success(
                new CategoryDetailResponse(updated),
                "Categoria atualizada com sucesso!"));
    }

    // DELETE
    @DeleteMapping("/{id}")
    public ResponseEntity<ApiResponse<Void>> deleteCategory(@PathVariable Long id) {
        categoryService.setDeleteCategory(id);
        return ResponseEntity.ok(ResponseUtil.success(null, "Categoria deletada com sucesso!"));
    }

    // ===== TYPE CATEGORY =====

    // GET ALL
    @GetMapping("/type")
    public ResponseEntity<ApiResponse<List<TypeCategoryResponse>>> listAllTypeCategories() {
        List<TypeCategoryResponse> response = typeCategoryRepository.findAll()
                .stream()
                .map(TypeCategoryResponse::new)
                .toList();

        return ResponseEntity.ok(ResponseUtil.success(response, "Tipos de categoria retornados com sucesso!"));
    }

    // GET BY ID
    @GetMapping("/type/{id}")
    public ResponseEntity<ApiResponse<TypeCategoryResponse>> searchTypeCategoryById(@PathVariable Long id) {
        TypeCategory typeCategory = typeCategoryRepository.findById(id)
                .orElseThrow(() -> new org.springframework.web.server.ResponseStatusException(
                        HttpStatus.NOT_FOUND, "Tipo de categoria não encontrado!"));

        return ResponseEntity.ok(ResponseUtil.success(
                new TypeCategoryResponse(typeCategory),
                "Tipo de categoria encontrado com sucesso!"));
    }
}