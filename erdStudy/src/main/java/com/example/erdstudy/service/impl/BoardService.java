package com.example.erdstudy.service.impl;

import com.example.erdstudy.domain.Board;
import com.example.erdstudy.domain.Member;
import com.example.erdstudy.dto.BoardDto;
import com.example.erdstudy.dto.BoardWriteForm;
import org.springframework.data.domain.Page;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.List;

public interface BoardService {
    BoardDto getBoard(Long id);

    List<BoardDto> getBoards();

    Page<BoardDto> getBoardByCategory(int size, String category);

    Page<BoardDto> getBoardPage(int page, String search);

    BoardDto createBoard(BoardWriteForm boardWriteForm, Member member, List<MultipartFile> files) throws IOException;

    BoardDto modifyBoard(BoardWriteForm boardWriteForm, Member member, Long b_id , List<MultipartFile> files) throws IOException;

    void deleteBoard(Long b_id, Member member);

}
