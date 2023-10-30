package com.example.erdstudy.service;

import com.example.erdstudy.domain.*;
import com.example.erdstudy.dto.BoardDto;
import com.example.erdstudy.dto.BoardWriteForm;
import com.example.erdstudy.repository.BoardRepository;
import com.example.erdstudy.repository.CommentRepository;
import com.example.erdstudy.repository.ImageFileRepository;
import com.example.erdstudy.repository.MemberRepository;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;


import java.io.IOException;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.BDDMockito.given;

@ExtendWith(MockitoExtension.class)
class BoardServiceImplTest {
    @InjectMocks
    private BoardServiceImpl boardService;

    @Mock
    private BoardRepository boardRepository;

    @Mock
    private ImageFileRepository imageFileRepository;

    @Mock
    private CommentRepository commentRepository;

    @Mock
    private MemberRepository memberRepository;

    private List<Board> create(int i) {
        int j=0;
        List<Board> boards = new ArrayList<>();
        while (j < i) {
            Board board = Board.builder()
                    .id((long) j)
                    .content("test" + j)
                    .title("test" + j)
                    .view(j)
                    .memberId((long) j)
                    .build();
            boards.add(board);
            j++;
        }
        return boards;
    }

    @Test
    @DisplayName("Board Detail Test")
    void getBoard() {
        Board board = new Board(1L, 5, "test", "test", 1L, LocalDateTime.now(), null);
        Member member = new Member(1L, "test", "test@test.com", "1234", Role.ROLE_MEMBER);
        int i = 0;

        List<Comment> comments = new ArrayList<>();

        while (i < 7) {
            Comment comment = new Comment((long) i, "test", 1L, 1L, LocalDateTime.now(), null);
            comments.add(comment);
            i++;
        }

        List<ImageFile> imageFiles = new ArrayList<>();


        given(boardRepository.findById(anyLong())).willReturn(Optional.of(board));
        given(memberRepository.findById(anyLong())).willReturn(Optional.of(member));
        given(commentRepository.findAllByBoardId(anyLong())).willReturn(comments);
        given(imageFileRepository.findAllByBoardId(anyLong())).willReturn(imageFiles);

        // when
        BoardDto result = boardService.getBoard(1L);

        // then
        assertThat(result.getTitle()).isEqualTo(board.getTitle());
    }

    @Test
    @DisplayName("Board List Page Test")
    void getBoardPage() {
        int page = 0;
        Pageable pageable = PageRequest.of(page, 10);

        Member member = Member.builder()
                .name("test")
                .email("test@test.com")
                .password("1234")
                .role(Role.ROLE_MEMBER)
                .build();

        List<Board> boards = create(5);
        Page<Board> boardPage = new PageImpl<>(boards, pageable, boards.size());

        System.out.println("boards[0].getContent :" + boards.get(0).getContent());


        given(boardRepository.findAll(pageable)).willReturn(boardPage);
        given(imageFileRepository.findAllByBoardId(anyLong())).willReturn(null);
        given(memberRepository.findById(anyLong())).willReturn(Optional.of(member));

        Page<BoardDto> result = boardService.getBoardPage(page, null);

        assertEquals("test0", result.getContent().get(0).getContent());
    }

    @Test
    @DisplayName("MultipartFile Null test")
    void createBoard() throws IOException {
        Member member = Member.builder()
                .id(1L)
                .role(Role.ROLE_MEMBER)
                .email("test@test.com")
                .password("1234")
                .build();

        Board board = Board.builder().build();

        BoardWriteForm boardWriteForm = new BoardWriteForm();
        boardWriteForm.setTitle("test");
        boardWriteForm.setContent("test_content");

        BoardDto result = boardService.createBoard(boardWriteForm, member, null);

        assertEquals("test", result.getTitle());
        assertNotEquals(board.getTitle(), result.getTitle());
    }

//    @Test
//    void modifyBoard() throws IOException {
//        Board board = new Board(1L, 5, "test", "test", 1L, LocalDateTime.now(), null);
//        Member member = new Member(1L, "test", "test@test.com", "1234", Role.ROLE_MEMBER);
//        int i = 0;
//
//        List<Comment> comments = new ArrayList<>();
//
//        while (i < 7) {
//            Comment comment = new Comment((long) i, "test", 1L, 1L);
//            comments.add(comment);
//            i++;
//        }
//
//        i=0;
//        List<ImageFile> imageFiles = new ArrayList<>();
//        while (i < 7) {
//            ImageFile imageFile = new ImageFile((long) i, 1L, "test" + i, "D:/test/test" + i, "test" + i);
//            i++;
//        }
//
//        List<MultipartFile> multipartFiles = new ArrayList<>();
//        i=0;
//        while (i<3) {
//            String fileName = "testImage1" + i; // 파일명
//            String contentType = "png"; // 파일 타입
//            String filePath = "src/test/resources/"+fileName+"."+contentType; // 파일 경로
//            FileInputStream fileInputStream = new FileInputStream();
//
//            //Mock 파일 생성
//            MockMultipartFile image1 = new MockMultipartFile(
//                    "images", //name
//                    fileName + "." + contentType, //originalFilename
//                    contentType,
//                    fileInputStream
//            );
//
//            multipartFiles.add(image1);
//            i++;
//        }
//
//        BoardWriteForm boardWriteForm = new BoardWriteForm();
//        boardWriteForm.setContent("testContent");
//        boardWriteForm.setTitle("testTitle");
//
//
//        given(boardRepository.findById(anyLong())).willReturn(Optional.of(board));
//        given(memberRepository.findById(anyLong())).willReturn(Optional.of(member));
//        given(commentRepository.findAllByBoardId(anyLong())).willReturn(comments);
//        given(imageFileRepository.findAllByBoardId(anyLong())).willReturn(imageFiles);
//
//        // when
//        BoardDto result = boardService.modifyBoard(boardWriteForm, 1L, 1L, multipartFiles);
//
//        assertThat(result.getTitle()).isEqualTo("testTitle");
//        assertThat(result.getFiles().get(0).getFilename()).isEqualTo("testImage10");
//    }

    @Test
    @DisplayName("Delete Board File Null Test")
    void modifyBoardTest() throws IOException {
        Board board = Board.builder()
                .id(1L)
                .title("title")
                .content("content")
                .view(3)
                .memberId(1L)
                .build();

        Member member = Member.builder()
                .id(1L)
                .email("test@test.com")
                .role(Role.ROLE_MEMBER)
                .build();

        BoardWriteForm boardWriteForm = new BoardWriteForm();
        boardWriteForm.setTitle("modify");
        boardWriteForm.setContent("modify_content");

//        given(board.getMemberId().equals(member.getId())).willReturn(true);

        given(boardRepository.findById(anyLong())).willReturn(Optional.of(board));
        given(imageFileRepository.findAllByBoardId(anyLong())).willReturn(null);
        given(commentRepository.findAllByBoardId(anyLong())).willReturn(null);

        BoardDto result = boardService.modifyBoard(boardWriteForm, member, board.getId(),null);

        assertEquals("modify", result.getTitle());
        assertEquals("modify_content", result.getContent());
        assertNotEquals(board.getTitle(), result.getTitle());
    }

    @Test
    void deleteBoard() {
        Board board = Board.builder()
                .id(1L)
                .memberId(1L)
                .title("title")
                .build();

        Member member = Member.builder()
                .id(1L)
                .build();


        given(boardRepository.findById(anyLong())).willReturn(Optional.of(board));
        given(imageFileRepository.findAllByBoardId(anyLong())).willReturn(null);
        given(imageFileRepository.findAllByBoardId(anyLong())).willReturn(null);

        boardService.deleteBoard(1L, member);
    }
}