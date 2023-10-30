package com.example.erdstudy.controller;

import com.example.erdstudy.dto.AuthenticationResponse;
import com.example.erdstudy.dto.LoginForm;
import com.example.erdstudy.dto.MemberDto;
import com.example.erdstudy.dto.SignUpForm;
import com.example.erdstudy.service.impl.JwtService;
import com.example.erdstudy.service.impl.MemberService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Date;

@RestController
@RequiredArgsConstructor
@Slf4j
@RequestMapping("/api/member")
@Tag(name = "Member API")
public class MemberController {
    private final MemberService memberService;

    @Operation(summary = "로그인 토큰 반환 , 매개변수 : email, password")
    @PostMapping("/login")
    public ResponseEntity<AuthenticationResponse> authenticate(@RequestBody LoginForm loginForm,
                                                               HttpServletResponse res) {
        AuthenticationResponse token = memberService.authenticate(loginForm);
        log.info("token : {}", token.getToken());

        Cookie cookie = new Cookie("token", token.getToken());
        cookie.setPath("/");
        cookie.setMaxAge(2*60*60);
        cookie.setHttpOnly(true);
        res.addCookie(cookie);

        log.info("cookie : {}" , cookie.getPath());
        log.info("cookie MaxAge : {}, current : {}", cookie.getMaxAge(), new Date(System.currentTimeMillis()));

        return ResponseEntity.ok(token);
    }

    @Operation(summary = "회원가입 -> 로그인 토큰 반환 , 매개변수 : name, email, password, validPassword")
    @PostMapping("/register")
    public ResponseEntity<AuthenticationResponse> register(@RequestBody SignUpForm sign, HttpServletResponse res) {
        AuthenticationResponse auth = memberService.register(sign);

        Cookie cookie = new Cookie("token", auth.getToken());
        cookie.setPath("/");
        cookie.setMaxAge(2*60*60);
        cookie.setHttpOnly(true);
        res.addCookie(cookie);

        log.info("cookie : {}" , cookie.getPath());
        log.info("cookie MaxAge : {}, current : {}", cookie.getMaxAge(), new Date(System.currentTimeMillis()));

        log.info("auth : {}" , auth.getToken());
        return ResponseEntity.status(HttpStatus.CREATED).body(auth);
    }

    @GetMapping("/info")
    @Operation(summary = "현재 접속된 유저정보 확인")
    public ResponseEntity<MemberDto> userInfo(@CookieValue(value = "token") String token) {
        MemberDto memberDto = memberService.userInfo(token);
        return new ResponseEntity<>(memberDto, HttpStatus.OK);
    }
}
