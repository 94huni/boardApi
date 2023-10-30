package com.example.erdstudy.repository;

import com.example.erdstudy.domain.Comment;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface CommentRepository extends JpaRepository<Comment, Long> {
    List<Comment> findAllByBoardId(Long b_id);
    List<Comment> findAllByMemberId(Long m_id);
}
