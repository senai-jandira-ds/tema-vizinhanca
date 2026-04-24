package com.tcc_vizinhanca.vizinhanca.repository.category;

import com.tcc_vizinhanca.vizinhanca.entity.category.Category;
import org.springframework.data.jpa.repository.JpaRepository;

public interface CategoryRepository extends JpaRepository<Category, Long> {
}
