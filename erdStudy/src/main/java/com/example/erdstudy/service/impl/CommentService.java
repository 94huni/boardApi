package com.example.erdstudy.service.impl;

import com.example.erdstudy.domain.Member;
import com.example.erdstudy.dto.CommentDto;

import java.util.List;

public interface CommentService {
    List<CommentDto> getMemberComment(Long m_id);
    List<CommentDto> getList(Long b_id);

    CommentDto createComment(String content, Member member, Long b_id);

    CommentDto modifyComment(String content, Member member, Long c_id);

    void deleteComment(Long c_id, Member member);
}
