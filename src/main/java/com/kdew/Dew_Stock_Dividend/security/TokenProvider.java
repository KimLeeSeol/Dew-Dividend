package com.kdew.Dew_Stock_Dividend.security;

import com.kdew.Dew_Stock_Dividend.service.MemberService;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.ExpiredJwtException;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import lombok.RequiredArgsConstructor;

import org.jsoup.helper.StringUtil;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;

import java.lang.reflect.Member;
import java.util.Date;
import java.util.List;

@Component
@RequiredArgsConstructor
public class TokenProvider {

    @Value("${spring.jwt.secret}")
    private String secretKey;

    private static final String KEY_ROLES = "roles"; // 상수기때문에 함수 내에서 이렇게 사용하는게 더 좋음
    private static final long TOKEN_EXPIRE_TIME = 1000 * 60 * 60; // 1시간

    private final MemberService memberService;

    /**
     * 토큰 생성(발급)
     */
    public String generateToken(String username, List<String> roles) {
        // 로그인한 사용자 이름과 사용자 권한을 input으로 받음

        // 사용자의 권한 정보 저장
        Claims claims = Jwts.claims().setSubject(username);
        claims.put(KEY_ROLES,roles);

        var now = new Date(); // 토큰 생성된 시간
        var expiredDate = new Date(now.getTime() + TOKEN_EXPIRE_TIME); // 토큰을 얼마나 유효하게 할 것인지 현재 시간부터 그 시간까지 시간을 만료 시간으로 잡을 것임, 지금은 한시간!

        // claims정보와 만료시간을 토큰에 넣어주기
        return Jwts.builder()
                .setClaims(claims)
                .setIssuedAt(now) // 토큰 생성 시간
                .setExpiration(expiredDate) // 토큰 만료 시간
                .signWith(SignatureAlgorithm.HS512, this.secretKey) // 사용할 암호화 알고리즘(ES512)과 비밀키
                .compact();
    }

    /**
     * security에 인증 정보 넣어주기
     * jwt토큰으로부터 인증 정보를 가져옴
     * 사용자 정보와 사용자 권한 정보를 포함
     */
    public Authentication getAuthentication(String jwt) {

        UserDetails userDetails = this.memberService.loadUserByUsername(this.getUsername(jwt)); // 이름 가져오기
        return new UsernamePasswordAuthenticationToken(userDetails, "", userDetails.getAuthorities());
    }


    /**
     * 생성된 토큰이 유효한지 확인
     */
    public String getUsername(String token) {
        return this.parseClaims(token).getSubject(); // username
    }

    public boolean validateToken(String token) {
        if (!StringUtils.hasText(token)) return false; // 만약 토큰이 빈값이면 토큰이 유효하지 않기 때문

        var claims = this.parseClaims(token);
        return !claims.getExpiration().before(new Date()); // 토큰 만료시간이 현재 시간보다 이전인지 아닌지 만료 여부 체크
    }

    /**
     * 토큰 파싱
     */
    private Claims parseClaims(String token) {
        // 파싱 과정에서 토큰 만료가 경과한 상태에서 토큰 파싱하려고 하면 예외 발생하기 때문에 예외처리 해주기
        try{
            return Jwts.parser().setSigningKey(this.secretKey).parseClaimsJws(token).getBody(); // claims 정보 가져오기
        } catch (ExpiredJwtException e) {
            return e.getClaims();
        }
    }
}
