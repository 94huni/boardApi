package com.example.erdstudy.controller;

import com.example.erdstudy.domain.Board;
import com.example.erdstudy.domain.Member;
import com.example.erdstudy.domain.Role;
import com.example.erdstudy.dto.BoardDto;
import com.example.erdstudy.dto.BoardWriteForm;
import com.example.erdstudy.repository.BoardRepository;
import com.example.erdstudy.repository.MemberRepository;
import com.example.erdstudy.service.BoardServiceImpl;
import com.example.erdstudy.service.impl.BoardService;
import com.example.erdstudy.service.impl.MemberService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.security.test.web.servlet.setup.SecurityMockMvcConfigurers;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MockMvcBuilder;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.context.WebApplicationContext;
import org.springframework.web.filter.CharacterEncodingFilter;

import java.io.IOException;

import static org.junit.jupiter.api.Assertions.*;

import static org.junit.jupiter.api.Assertions.*;
import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@ExtendWith(SpringExtension.class)
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.MOCK)
class BoardControllerTest {
    @InjectMocks
    BoardController boardController;

    @Mock
    BoardService boardService;
    @Mock
    MemberService memberService;

    @Mock
    MemberRepository memberRepository;

    @Autowired
    private WebApplicationContext wac;

    MockMvc mockMvc;

    @BeforeEach
    public void beforeEach() {
        this.mockMvc = MockMvcBuilders
                .webAppContextSetup(wac)
                .addFilter(new CharacterEncodingFilter("UTF-8", true))
                .apply(SecurityMockMvcConfigurers.springSecurity())
                .build();
    }

    @Test
    void getBoardPage() {
    }

    @Test
    @WithMockUser(username = "username", authorities = {"ROLE_MEMBER"})
    void getBoard() throws Exception {
        BoardDto board = BoardDto.builder()
                .title("test")
                .username("test_user")
                .content("test_contents")
                .id(1L)
                .build();

        given(boardService.getBoard(1L)).willReturn(board);

        this.mockMvc.perform(get("/api/board/1"))
                .andExpect(status().isOk())
                .andDo(print());
    }

    @Test
    @WithMockUser(username = "test", roles = "ROLE_MEMBER", authorities = {"ROLE_MEMBER"})
    void createBoard() throws Exception {
    }

    @Test
    void updateBoard() {
    }

    @Test
    void deleteBoard() {
    }
}