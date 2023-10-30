package com.example.erdstudy.dto;

import lombok.Getter;
import lombok.Setter;

@Getter @Setter
public class BoardWriteForm {
    private String title;
    private String content;
    private String category;
}
