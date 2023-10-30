package com.example.erdstudy.service;

import com.example.erdstudy.domain.Board;
import com.example.erdstudy.domain.Comment;
import com.example.erdstudy.domain.Member;
import com.example.erdstudy.dto.CommentDto;
import com.example.erdstudy.exception.CustomException;
import com.example.erdstudy.repository.BoardRepository;
import com.example.erdstudy.repository.CommentRepository;
import com.example.erdstudy.repository.MemberRepository;
import com.example.erdstudy.service.impl.CommentService;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Service("CommentService")
@Transactional
@RequiredArgsConstructor
public class CommentServiceImpl implements CommentService {
    private final CommentRepository commentRepository;
    private final MemberRepository memberRepository;
    private final BoardRepository boardRepository;

    private CommentDto dto(Member member, Comment comment) {
        return CommentDto.builder()
                .c_id(comment.getId())
                .content(comment.getContent())
                .username(member.getUsername())
                .createTime(comment.getCreateTime())
                .updateTime(comment.getUpdateTime())
                .build();
    }

    @Override
    public List<CommentDto> getMemberComment(Long m_id) {
        Member member = memberRepository.findById(m_id).orElseThrow(() -> new RuntimeException("회원 정보 없음"));
        List<Comment> comments = commentRepository.findAllByMemberId(member.getId());
        List<CommentDto> commentDtos = new ArrayList<>();
        for (Comment comment : comments) {
            CommentDto commentDto = dto(member, comment);
            commentDtos.add(commentDto);
        }
        return commentDtos;
    }

    @Override
    public List<CommentDto> getList(Long b_id) {
        boardRepository.findById(b_id).orElseThrow(() -> new RuntimeException("게시판 정보 없음"));
        List<Comment> comments = commentRepository.findAllByBoardId(b_id);
        List<CommentDto> commentDtos = new ArrayList<>();

        for (Comment comment : comments) {
            Member member = memberRepository.findById(comment.getMemberId()).orElseThrow(() -> new RuntimeException("회원 정보 없음"));
            CommentDto commentDto = dto(member, comment);
            commentDtos.add(commentDto);
        }
        return commentDtos;
    }

    @Override
    public CommentDto createComment(String content, Member member, Long b_id) {
        Board board = boardRepository.findById(b_id).orElseThrow(()-> new CustomException("게시글 정보 없음", HttpStatus.NOT_FOUND));

        Comment comment = Comment.builder()
                .boardId(board.getId())
                .createTime(LocalDateTime.now())
                .memberId(member.getId())
                .content(content)
                .build();

        commentRepository.save(comment);
        return dto(member, comment);
    }

    @Override
    public CommentDto modifyComment(String content, Member member, Long c_id) {
        Comment comment = commentRepository.findById(c_id).orElseThrow(() -> new CustomException("게시글 정보 없음", HttpStatus.NOT_FOUND));

        if(!member.getId().equals(comment.getMemberId())){
            throw new CustomException("작성자만 수정 가능", HttpStatus.UNAUTHORIZED);
        }

        Comment result = comment.toBuilder()
                .content(content)
                .updateTime(LocalDateTime.now())
                .build();

        commentRepository.save(result);
        return dto(member, result);
    }

    @Override
    public void deleteComment(Long c_id, Member member) {
        Comment comment = commentRepository.findById(c_id).orElseThrow(()->new RuntimeException("댓글 정보 없음"));

        if (!comment.getMemberId().equals(member.getId()))
            throw new CustomException("작성자만 삭제 가능", HttpStatus.UNAUTHORIZED);

        commentRepository.delete(comment);
    }
}
