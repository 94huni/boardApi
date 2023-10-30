package com.example.erdstudy.service;

import com.example.erdstudy.domain.Member;
import com.example.erdstudy.domain.Role;
import com.example.erdstudy.dto.AuthenticationResponse;
import com.example.erdstudy.dto.LoginForm;
import com.example.erdstudy.dto.MemberDto;
import com.example.erdstudy.dto.SignUpForm;
import com.example.erdstudy.exception.CustomException;
import com.example.erdstudy.repository.MemberRepository;
import com.example.erdstudy.service.impl.JwtService;
import com.example.erdstudy.service.impl.MemberService;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;



@Service("MemberService")
@RequiredArgsConstructor
@Transactional
public class MemberServiceImpl implements MemberService{
    private final MemberRepository memberRepository;
    private final JwtService jwtService;
    private final AuthenticationManager authenticationManager;
    private final PasswordEncoder passwordEncoder;

    public AuthenticationResponse authenticate(LoginForm loginForm) {
        authenticationManager.authenticate(new UsernamePasswordAuthenticationToken(
                loginForm.getEmail(),
                loginForm.getPassword()
        ));

        var member = memberRepository.findByEmail(loginForm.getEmail()).orElseThrow(()->new CustomException("회원 정보 없음", HttpStatus.NOT_FOUND));
        var jwt = jwtService.generateToken(member);
        return AuthenticationResponse.builder()
                .token(jwt)
                .build();
    }

    @Override
    public MemberDto userInfo(String token) {
        Member member = memberRepository.findByEmail(jwtService.getEmail(token)).orElseThrow(() -> new CustomException("회원 정보 없음", HttpStatus.NOT_FOUND));
        return MemberDto.builder()
                .id(member.getId())
                .email(member.getEmail())
                .role(member.getRole().name())
                .name(member.getName())
                .build();
    }

    @Transactional
    public AuthenticationResponse register(SignUpForm request) {

        if (memberRepository.existsByEmail(request.getEmail()))
            throw new CustomException("이미 가입된 이메일 입니다", HttpStatus.BAD_REQUEST);

        if(request.getPassword().equals(request.getValidPassword())){

            var user = Member.builder()
                    .name(request.getName())
                    .email(request.getEmail())
                    .password(passwordEncoder.encode(request.getPassword()))
                    .role(Role.ROLE_MEMBER)
                    .build();

            memberRepository.save(user);

            var jwtToken = jwtService.generateToken(user);
            return AuthenticationResponse.builder()
                    .token(jwtToken)
                    .build();
        }

        throw new CustomException("비밀번호가 다릅니다", HttpStatus.BAD_REQUEST);

    }

    @Transactional
    public String getEmail(String token) {
        return jwtService.extractUsername(token);
    }

    @Override
    public Member getMemberByEmail(String email) {
        return memberRepository.findByEmail(email).orElseThrow(() -> new CustomException("유저 정보 없음", HttpStatus.NOT_FOUND));
    }


}
