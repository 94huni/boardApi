package com.example.erdstudy.domain;

import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name = "file")
@Getter @Setter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor
@Builder
public class ImageFile {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "b_id")
    private Long boardId;

    @Column
    private String filename;

    @Column
    private String filepath;

    @Column
    private String originalName;
}
