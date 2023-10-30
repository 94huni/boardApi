package com.example.erdstudy.service;

import com.example.erdstudy.domain.*;
import com.example.erdstudy.dto.BoardDto;
import com.example.erdstudy.dto.BoardWriteForm;
import com.example.erdstudy.exception.CustomException;
import com.example.erdstudy.repository.*;
import com.example.erdstudy.service.impl.BoardService;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.IOException;
import java.rmi.RemoteException;
import java.time.LocalDateTime;
import java.util.*;

@Service("BoardService")
@RequiredArgsConstructor
public class BoardServiceImpl implements BoardService {
    private final BoardRepository boardRepository;
    private final MemberRepository memberRepository;
    private final ImageFileRepository fileRepository;
    private final CommentRepository commentRepository;
    private final CategoryRepository categoryRepository;

    private BoardDto dto(Member member, Board board, List<ImageFile> files, Category category) {
        return BoardDto.builder()
                .title(board.getTitle())
                .content(board.getContent())
                .createTime(board.getCreateTime())
                .updateTime(board.getUpdateTime())
                .username(member.getUsername())
                .category(category.getCategory())
                .views(board.getView())
                .id(board.getId())
                .files(files)
                .build();
    }

    private void countView(Board board) {
        board.setView(board.getView() + 1);
    }

    private ImageFile upload(MultipartFile multipartFile, Long b_id) throws IOException {
        String directory = "D:/workspaces/";
        String originalName = multipartFile.getOriginalFilename();
        String filepath = directory + UUID.randomUUID() + "-" + originalName;

        File file = new File(filepath);
        multipartFile.transferTo(file);


        return ImageFile.builder()
                .filename(file.getName())
                .filepath(filepath)
                .boardId(b_id)
                .originalName(originalName)
                .build();

    }

    private void deleteFile(ImageFile file) {

        File deleteFile = new File(file.getFilepath());
        boolean delete = deleteFile.delete();
        
        if (!delete) {
            throw new CustomException("파일 삭제 실패", HttpStatus.BAD_REQUEST);
        }
    }

    @Transactional
    @Override
    public BoardDto getBoard(Long id) {
        Board board = boardRepository.findById(id).orElseThrow(() -> new RuntimeException("게시글 정보 없음"));
        countView(board);
        Long b_id = board.getId();
        Member member = memberRepository.findById(board.getMemberId()).orElseThrow(() -> new RuntimeException("유저 정보 없음"));
        List<ImageFile> files = fileRepository.findAllByBoardId(b_id);
        Category category = categoryRepository.findByBoardId(b_id);

        return dto(member, board, files, category);
    }

    @Transactional
    @Override
    public List<BoardDto> getBoards() {
        List<BoardDto> boardDtos = new ArrayList<>();
        List<Board> boards = boardRepository.findAll();

        for(Board board : boards) {
            List<ImageFile> files = fileRepository.findAllByBoardId(board.getId());
            List<Comment> comments = commentRepository.findAllByBoardId(board.getId());
            Member member = memberRepository.findById(board.getMemberId()).orElseThrow(() -> new CustomException("회원 정보 없음" ,HttpStatus.NOT_FOUND));
            Category category = categoryRepository.findByBoardId(board.getId());

            boardDtos.add(dto(member, board, files, category));
        }

        return boardDtos;
    }

    @Override
    public Page<BoardDto> getBoardByCategory(int page ,String category) {
        Pageable pageable = PageRequest.of(page, 10);
        Page<Category> categories = categoryRepository.findByCategory(category, pageable);
        List<BoardDto> boardDtos = new ArrayList<>();

        for (Category ca : categories.getContent()) {
            Board board = boardRepository.findById(ca.getBoardId())
                    .orElseThrow(() -> new CustomException("게시판 정보 없음", HttpStatus.NOT_FOUND));

            List<ImageFile> imageFiles = fileRepository.findAllByBoardId(board.getId());

            Member member = memberRepository.findById(board.getMemberId())
                    .orElseThrow(() -> new CustomException("회원 정보 없음", HttpStatus.NOT_FOUND));

            boardDtos.add(dto(member, board, imageFiles, ca));
        }

        return new PageImpl<>(boardDtos, pageable, categories.getTotalPages());
    }

    @Override
    @Transactional
    public Page<BoardDto> getBoardPage(int page, String search) {
        Pageable pageable = PageRequest.of(page, 10);
        List<BoardDto> boardDtos = new ArrayList<>();

        if (search == null) {
            Page<Board> boards = boardRepository.findAll(pageable);
            for (Board board : boards) {
                List<ImageFile> imageFile = fileRepository.findAllByBoardId(board.getId());
                Member member = memberRepository.findById(board.getMemberId()).orElseThrow();
                Category category = categoryRepository.findByBoardId(board.getId());
                BoardDto boardDto = dto(member, board, imageFile, category);
                boardDtos.add(boardDto);
            }
            return new PageImpl<>(boardDtos, pageable, boards.getTotalPages());
        }

        Page<Board> boards = boardRepository.findByTitleContaining(search, pageable);
        for(Board board : boards) {
            List<ImageFile> imageFile = fileRepository.findAllByBoardId(board.getId());
            Member member = memberRepository.findById(board.getMemberId()).orElseThrow();
            Category category = categoryRepository.findByBoardId(board.getId());
            BoardDto boardDto = dto(member, board, imageFile, category);
            boardDtos.add(boardDto);
        }

        return new PageImpl<>(boardDtos, pageable, boards.getTotalPages());
    }


    @Transactional
    @Override
    public BoardDto createBoard(BoardWriteForm boardWriteForm, Member member, List<MultipartFile> files) throws IOException {

        if (member.getRole().equals(Role.ROLE_MEMBER) || member.getRole().equals(Role.ROLE_ADMIN)){
            Board board = Board.builder()
                    .memberId(member.getId())
                    .title(boardWriteForm.getTitle())
                    .content(boardWriteForm.getContent())
                    .view(1)
                    .createTime(LocalDateTime.now())
                    .build();
            Board newBoard = boardRepository.save(board);
            Long b_id = newBoard.getId();

            Category category = Category.builder()
                    .boardId(b_id)
                    .category(boardWriteForm.getCategory())
                    .build();

            categoryRepository.save(category);

            if(files != null){
                List<ImageFile> imageFiles = new ArrayList<>();
                for(MultipartFile multipartFile : files) {
                    imageFiles.add(upload(multipartFile, b_id));
                }

                fileRepository.saveAll(imageFiles);

                return dto(member, board, imageFiles, category);
            }

            return dto(member, board, null, category);
        }
        throw new CustomException("권한 없음", HttpStatus.UNAUTHORIZED);


    }

    @Transactional
    @Override
    public BoardDto modifyBoard(BoardWriteForm boardWriteForm, Member member, Long b_id, List<MultipartFile> files) throws IOException {
        Board board = boardRepository.findById(b_id).orElseThrow(() -> new CustomException("게시글 정보 없음", HttpStatus.NOT_FOUND));

        if (!board.getMemberId().equals(member.getId())) {
            throw new CustomException("자기 글만 수정 가능" ,HttpStatus.UNAUTHORIZED);
        }

        List<ImageFile> imageFiles = fileRepository.findAllByBoardId(board.getId());
        List<Comment> comments = commentRepository.findAllByBoardId(board.getId());
        Category category = categoryRepository.findByBoardId(board.getId());

        if (files != null && !files.isEmpty()) {
            fileRepository.deleteAll(imageFiles);
            for (ImageFile delete : imageFiles) {
                deleteFile(delete);
            }
            List<ImageFile> currentFiles = new ArrayList<>();
            for (MultipartFile file : files) {
                ImageFile imageFile = upload(file, board.getId());
                currentFiles.add(imageFile);
            }

            imageFiles.clear();
            imageFiles.addAll(currentFiles);
        }

        Board result = board.toBuilder()
                .updateTime(LocalDateTime.now())
                .title(boardWriteForm.getTitle())
                .content(boardWriteForm.getContent()).build();

        Category categoryResult = category.toBuilder()
                .category(boardWriteForm.getCategory())
                .build();

        boardRepository.save(result);
        fileRepository.saveAll(imageFiles);

        return dto(member, result, imageFiles, categoryResult);
    }

    @Transactional
    @Override
    public void deleteBoard(Long b_id, Member member) {
        Board board = boardRepository.findById(b_id).orElseThrow(() -> new RuntimeException("게시글 정보 없음"));
        List<ImageFile> imageFiles = fileRepository.findAllByBoardId(board.getId());
        Category category = categoryRepository.findByBoardId(board.getId());

        if(!board.getMemberId().equals(member.getId()))
            throw new CustomException("자신의 글만 삭제 가능", HttpStatus.UNAUTHORIZED);

        if (imageFiles != null){
            for(ImageFile imageFile : imageFiles) {
                deleteFile(imageFile);
            }
            fileRepository.deleteAll(imageFiles);
        }
        categoryRepository.delete(category);
        boardRepository.deleteById(b_id);
    }
}
