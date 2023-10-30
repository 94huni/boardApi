package com.example.erdstudy.controller;

import com.example.erdstudy.domain.Member;
import com.example.erdstudy.dto.AuthenticationResponse;
import com.example.erdstudy.dto.BoardDto;
import com.example.erdstudy.dto.BoardWriteForm;
import com.example.erdstudy.service.impl.BoardService;
import com.example.erdstudy.service.impl.JwtService;
import com.example.erdstudy.service.impl.MemberService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.annotation.Nullable;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.client.ResponseErrorHandler;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.List;

@Slf4j
@RestController
@RequiredArgsConstructor
@RequestMapping("/api/board")
@Tag(name = "Board API")
public class BoardController {
    private final BoardService boardService;
    private final MemberService memberService;
    private final JwtService jwtService;

    @GetMapping("/all")
    @Operation(summary = "게시판 리스트")
    public ResponseEntity<List<BoardDto>> getBoards() {
        return new ResponseEntity<>(boardService.getBoards(), HttpStatus.OK);
    }

    @GetMapping("/list")
    @Operation(summary = "게시판 리스트 페이지 + 제목으로 검색")
    public ResponseEntity<Page<BoardDto>> getBoardPage(@RequestParam(defaultValue = "0") @Parameter(name = "page", description = "페이지") int page,
                                                       @RequestParam @Nullable @Parameter(name = "search", description = "검색어, 입력없으면 전체페이지") String search) {

        log.info("Search Keyword : {}" , search);
        return new ResponseEntity<>(boardService.getBoardPage(page, search), HttpStatus.OK);
    }

    @GetMapping("")
    @Operation(summary = "카테고리별 검색, 매개변수 : 카테고리명, 페이지넘버")
    public ResponseEntity<Page<BoardDto>> getBoardByCategory(@RequestParam(value = "category", defaultValue = "test") String category,
                                                             @RequestParam(value = "page", defaultValue = "0") int page) {
        return new ResponseEntity<>(boardService.getBoardByCategory(page, category), HttpStatus.OK);
    }

    @Operation(summary = "게시판 상세, 매개변수 : boardId")
    @GetMapping("/{b_id}")
    public ResponseEntity<BoardDto> getBoard(@PathVariable("b_id") Long b_id) {
        BoardDto boardDto = boardService.getBoard(b_id);
        log.info("Writer : {}, Board Id : {} , Board Title : {}", boardDto.getUsername(), boardDto.getId(), boardDto.getTitle());
        return new ResponseEntity<>(boardDto, HttpStatus.OK);
    }

    @PostMapping(value = "", consumes = "multipart/form-data")
    @Operation(summary = "게시판 글 등록, 매개변수 : token, title, content, file")
    public ResponseEntity<BoardDto> createBoard(@CookieValue(value = "token", required = false) @Parameter(name = "jwt", description = "JSON web token", example = "jwt") String token,
                                                BoardWriteForm boardWriteForm,
                                                @RequestPart(required = false) List<MultipartFile> multipartFiles) throws IOException {

        if (token != null) {
            log.info("getValue : {}", token);
            String email = jwtService.getEmail(token);

            Member member = memberService.getMemberByEmail(email);
            log.info("Authorities : {}", member.getAuthorities());


            log.info("email : {}", email);
            BoardDto boardDto = boardService.createBoard(boardWriteForm, member, multipartFiles);

            return new ResponseEntity<>(boardDto, HttpStatus.CREATED);
        }
        return new ResponseEntity<>(HttpStatus.FORBIDDEN);
    }

    @PutMapping(value = "/{b_id}", consumes = "multipart/form-data")
    @Operation(summary = "게시판 정보 수정, 매개변수 : boardId, jwt, title, content, file")
    public ResponseEntity<BoardDto> updateBoard(@PathVariable("b_id") /*@Parameter(name = "board_id", description = "board id", example = "1")*/ Long b_id,
                                                @CookieValue(value = "token") @Parameter(name = "jwt", description = "JSON web token", example = "jwt") String token,
                                                BoardWriteForm boardWriteForm,
                                                @RequestPart(required = false) List<MultipartFile> multipartFiles) throws IOException {

        Member member = memberService.getMemberByEmail(jwtService.getEmail(token));
        log.info("Email : {}" , member.getEmail());
        BoardDto boardDto = boardService.modifyBoard(boardWriteForm, member, b_id, multipartFiles);

        return new ResponseEntity<>(boardDto, HttpStatus.OK);
    }

    @DeleteMapping("/{b_id}")
    @Operation(summary = "게시판 삭제, 매개변수 : boardId, jwt")
    public ResponseEntity<String> deleteBoard(@PathVariable("b_id") Long b_id,
                                              @CookieValue(value = "token") @Parameter(name = "jwt", description = "JSON web token", example = "jwt") String token) {

        boardService.deleteBoard(b_id, memberService.getMemberByEmail(jwtService.getEmail(token)));

        return new ResponseEntity<>("삭제 성공", HttpStatus.NO_CONTENT);
    }
}
