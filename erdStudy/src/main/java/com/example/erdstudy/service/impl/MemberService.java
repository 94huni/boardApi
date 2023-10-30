package com.example.erdstudy.service.impl;

import com.example.erdstudy.domain.Member;
import com.example.erdstudy.dto.AuthenticationResponse;
import com.example.erdstudy.dto.LoginForm;
import com.example.erdstudy.dto.MemberDto;
import com.example.erdstudy.dto.SignUpForm;

public interface MemberService {
    AuthenticationResponse authenticate(LoginForm loginForm);

    AuthenticationResponse register(SignUpForm sign);

    String getEmail(String token);

    MemberDto userInfo(String token);

    Member getMemberByEmail(String email);
}
