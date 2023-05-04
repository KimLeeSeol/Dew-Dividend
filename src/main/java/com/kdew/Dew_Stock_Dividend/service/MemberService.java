package com.kdew.Dew_Stock_Dividend.service;

import com.kdew.Dew_Stock_Dividend.exception.impl.AlreadyExistUserException;
import com.kdew.Dew_Stock_Dividend.model.Auth;
import com.kdew.Dew_Stock_Dividend.model.MemberEntity;
import com.kdew.Dew_Stock_Dividend.repository.MemberRepository;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.rmi.AlreadyBoundException;

@Slf4j
@Service
@AllArgsConstructor
public class MemberService implements UserDetailsService {

    private final PasswordEncoder passwordEncoder;
    private final MemberRepository memberRepository;

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        return this.memberRepository.findByUsername(username)
                .orElseThrow( () -> new UsernameNotFoundException("couldn't find user -> " + username));
    }

    //회원가입
    public MemberEntity register(Auth.SignUp member) {

        boolean exists = this.memberRepository.existsByUsername(member.getUsername()); // 해당 아이디와 동일한 아이디가 있는지 확인
        if (exists) {
            throw new AlreadyExistUserException();
        }

        member.setPassword(this.passwordEncoder.encode(member.getPassword())); // 안전하게 인코딩된 패스워드 값을 set함
        var result = this.memberRepository.save(member.toEntity());

        return result;
    }

    // 로그인 검증
    public MemberEntity authenticate(Auth.SignIn member) {

        var user = this.memberRepository.findByUsername(member.getUsername())// 멤버 엔티티 값 가져옴
                .orElseThrow(() -> new RuntimeException("존재하지 않는 ID입니다."));

        if (!this.passwordEncoder.matches(member.getPassword(), user.getPassword())) {
            // 인코딩된 값을 매치해서 서로 비교해서 일치하지 않으면
            throw new RuntimeException("비밀번호가 일치하지 않습니다.");
        }

        // 정상
        return user;
    }
}
