package com.example.erdstudy.repository;

import com.example.erdstudy.domain.Category;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface CategoryRepository extends JpaRepository<Category, Long> {
    Category findByBoardId(Long id);

    Page<Category> findByCategory(String category, Pageable pageable);
}
