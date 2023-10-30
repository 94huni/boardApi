package com.example.erdstudy.domain;

import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;

@Table(name = "comment")
@Entity
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor
@Builder(toBuilder = true)
@Getter @Setter
public class Comment {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column
    private String content;

    @Column(name = "b_id")
    private Long boardId;

    @Column(name = "m_id")
    private Long memberId;

    private LocalDateTime createTime;
    private LocalDateTime updateTime;

}
