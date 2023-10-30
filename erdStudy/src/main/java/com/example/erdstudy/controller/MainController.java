package com.example.erdstudy.controller;

import com.example.erdstudy.service.BoardServiceImpl;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/main")
@Tag(name = "Main")
public class MainController {
    private final BoardServiceImpl boardService;
    @GetMapping("")
    public String index() {
        return "";
    }
}
