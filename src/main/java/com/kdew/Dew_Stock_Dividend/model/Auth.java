package com.kdew.Dew_Stock_Dividend.model;

import lombok.Data;

import java.util.List;

public class Auth {

    //로그인
    @Data
    public static class SignIn {
        private String username;
        private String password;

    }

    //회원가입
    @Data
    public static class SignUp {
        private String username;
        private String password;

        // 어떤 권한을 줄 것인지
        // 일반회원이 가입하는 경로에서는 일반 회원 권한
        // 관리자가 회원가입하는 페이지에서는 관리자들이 사용할 수 있는 권한들
        private List<String> roles;

        // MemberEntity로 바꿀 수 있는 메소드
        public MemberEntity toEntity() {
            return MemberEntity.builder()
                    .username(this.username)
                    .password(this.password)
                    .rolse(this.roles)
                    .build();
        }
    }
}
