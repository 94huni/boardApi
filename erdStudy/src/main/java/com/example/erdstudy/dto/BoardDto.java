package com.example.erdstudy.dto;

import com.example.erdstudy.domain.Comment;
import com.example.erdstudy.domain.ImageFile;
import lombok.*;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;

@Getter @Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class BoardDto {
    private Long id;
    private String username;
    private String title;
    private String content;
    private String category;
    private int views;
    private int commentCount;
    private List<ImageFile> files;
    private LocalDateTime createTime;
    private LocalDateTime updateTime;
}
