package com.kdew.Dew_Stock_Dividend.security;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.jsoup.helper.StringUtil;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;
import org.springframework.util.ObjectUtils;
import org.springframework.util.StringUtils;
import org.springframework.web.filter.OncePerRequestFilter;

import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

@Slf4j
@Component
@RequiredArgsConstructor
public class JwtAuthenticationFilter extends OncePerRequestFilter {


    public static final String TOKEN_HEADER = "Authorization"; // 토큰은 http protocol에서 헤더에 포함됨
    public static final String TOKEN_PREFIX = "Bearer "; // 인증 타입, jwt는 토큰 앞에 Bearer을 붙임

    private final TokenProvider tokenProvider; // 토큰이 유효한지 확인하기 위해

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain) throws ServletException, IOException {

        String token = resolveTokenFromRequest(request);

        // 토큰이 유효한지 확인
        if (StringUtils.hasText(token) && this.tokenProvider.validateToken(token)) {
            Authentication auth = this.tokenProvider.getAuthentication(token);
            SecurityContextHolder.getContext().setAuthentication(auth);

        }

        //필터가 연속적으로 실행되게
        filterChain.doFilter(request,response);

    }

    // 토큰이 있는지 확인
    private String resolveTokenFromRequest(HttpServletRequest request) {
        String token = request.getHeader(TOKEN_HEADER);

        if (!ObjectUtils.isEmpty(token) && token.startsWith(TOKEN_PREFIX)) {
            return token.substring(TOKEN_PREFIX.length());
        }
        return null;
    }
}
