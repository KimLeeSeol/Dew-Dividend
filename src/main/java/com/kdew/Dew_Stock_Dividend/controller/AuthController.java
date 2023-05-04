package com.kdew.Dew_Stock_Dividend.controller;

import com.kdew.Dew_Stock_Dividend.model.Auth;
import com.kdew.Dew_Stock_Dividend.security.TokenProvider;
import com.kdew.Dew_Stock_Dividend.service.MemberService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@Slf4j
@RestController
@RequestMapping("/auth")
@RequiredArgsConstructor
public class AuthController {

    private final MemberService memberService;
    private final TokenProvider tokenProvider;

    // 회원가입용 API
    @PostMapping("/signup")
    public ResponseEntity<?> signup(@RequestBody Auth.SignUp request) {
        // 사용자에게 받은 request 정보를 memberrepository에 저장해주면 됨

        var result = this.memberService.register(request);
        return ResponseEntity.ok(result);
    }

    // 로그인용 API
    @PostMapping("/signin")
    public ResponseEntity<?> signin(@RequestBody Auth.SignIn request) {
        // 아이디와 비밀번호가 일치한지 검증
        var member = this.memberService.authenticate(request);
        // 아이디와 비밀번호가 일치하면 token 생성해서 반환
        var token = this.tokenProvider.generateToken(member.getUsername(),member.getRolse());

        return ResponseEntity.ok(token);
    }
}
