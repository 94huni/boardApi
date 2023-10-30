package com.example.erdstudy.dto;

import lombok.*;

import java.time.LocalDateTime;

@Getter @Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder(toBuilder = true)
public class CommentDto {
    private Long c_id;
    private String username;
    private String content;
    private LocalDateTime createTime;
    private LocalDateTime updateTime;
}
