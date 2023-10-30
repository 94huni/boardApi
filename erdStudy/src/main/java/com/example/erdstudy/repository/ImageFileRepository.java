package com.example.erdstudy.repository;

import com.example.erdstudy.domain.ImageFile;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ImageFileRepository extends JpaRepository<ImageFile, Long> {
    List<ImageFile> findAllByBoardId(Long b_id);
}
