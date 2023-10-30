package com.example.erdstudy.controller;

import com.example.erdstudy.domain.Member;
import com.example.erdstudy.dto.CommentDto;
import com.example.erdstudy.dto.CommentWriteForm;
import com.example.erdstudy.service.impl.CommentService;
import com.example.erdstudy.service.impl.JwtService;
import com.example.erdstudy.service.impl.MemberService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RequiredArgsConstructor
@Slf4j
@RestController
@RequestMapping("/api/comment")
@Tag(name = "Comment API")
public class CommentController {
    private final CommentService commentService;
    private final MemberService memberService;
    private final JwtService jwtService;

    @GetMapping("/board/{b_id}")
    @Operation(summary = "게시글의 댓글 목록, 매개변수 : boardId")
    public ResponseEntity<List<CommentDto>> getComment(@PathVariable("b_id") /*@Parameter(name = "board_id", description = "board id", example = "1")*/ Long b_id) {
        List<CommentDto> commentDto = commentService.getList(b_id);
        return new ResponseEntity<>(commentDto, HttpStatus.OK);
    }

    @Operation(summary = "회원의 댓글 목록, 매개변수 : memberId")
    @GetMapping("/member/{m_id}")
    public ResponseEntity<List<CommentDto>> getMemberComment(@PathVariable("m_id") /*@Parameter(name = "memberId", description = "member id", example = "1")*/ Long m_id) {
        List<CommentDto> commentDtos = commentService.getMemberComment(m_id);

        return new ResponseEntity<>(commentDtos, HttpStatus.OK);
    }

    @Operation(summary = "댓글 등록, 매개변수 : content, jwt, boardId")
    @PostMapping("/{b_id}")
    public ResponseEntity<CommentDto> createComment(@RequestBody CommentWriteForm content,
                                                    @CookieValue(value = "token") @Parameter(name = "jwt", description = "JSON web token", example = "jwt") String token,
                                                    @PathVariable("b_id") @Parameter(name = "b_id", description = "board Id", example = "1") Long b_id) {
        Member member = memberService.getMemberByEmail(jwtService.getEmail(token));
        log.info("Member Email : {}", member.getEmail());

        CommentDto commentDto = commentService.createComment(content.getContent(), member, b_id);

        return new ResponseEntity<>(commentDto, HttpStatus.CREATED);
    }

    @Operation(summary = "댓글 정보 수정, 매개변수 : content, jwt, commentId")
    @PutMapping("/{c_id}")
    public ResponseEntity<CommentDto> modifyComment(@RequestBody CommentWriteForm content,
                                                    @CookieValue(value = "token") @Parameter(name = "jwt", description = "JSON web token", example = "jwt") String token,
                                                    @PathVariable("c_id") /*@Parameter(name = "comment_id", description = "comment id", example = "1")*/ Long c_id) {
        Member member = memberService.getMemberByEmail(jwtService.getEmail(token));
        log.info("Member Email : {}" ,member.getEmail());
        CommentDto commentDto = commentService.modifyComment(content.getContent(), member, c_id);

        return new ResponseEntity<>(commentDto, HttpStatus.OK);
    }

    @Operation(summary = "댓글 삭제, 매개변수 : commentId, jwt")
    @DeleteMapping("/{c_id}")
    public ResponseEntity<String> deleteComment(@PathVariable("c_id") /*@Parameter(name = "commentId", description = "comment id", example = "1")*/ Long c_id,
                                                @CookieValue(value = "token") @Parameter(name = "jwt", description = "JSON web token", example = "jwt") String token) {
        Member member = memberService.getMemberByEmail(jwtService.getEmail(token));
        commentService.deleteComment(c_id, member);

        return new ResponseEntity<>("삭제 되었습니다.", HttpStatus.NO_CONTENT);
    }
}
