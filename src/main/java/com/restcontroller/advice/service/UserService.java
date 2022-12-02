package com.restcontroller.advice.service;

import com.restcontroller.advice.domain.User;
import com.restcontroller.advice.exception.AppException;
import com.restcontroller.advice.exception.ErrorCode;
import com.restcontroller.advice.repository.UserRepository;
import com.restcontroller.advice.utils.JwtTokenUtil;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class UserService {

    private final UserRepository userRepository;
    private final BCryptPasswordEncoder encoder;
    @Value("${jwt.token.secret")
    private String key;

    public String join(String userName, String password) {
        // user name 중복 체크
        userRepository.findByUserName(userName)
                .ifPresent(user -> {
                    throw new AppException(ErrorCode.USERNAME_DUPLICATED, userName + "은 이미 존재합니다.");
                });// 만약 있다면

        // 저장
        User user = User.builder()
                .userName(userName)
                .password(encoder.encode(password))
                .build();
        userRepository.save(user);

        return "SUCCESS";
    }

    public String login(String userName, String password) {
        // userName이 없을 때
        User selectedUser = userRepository.findByUserName(userName)
                .orElseThrow(() -> new AppException(ErrorCode.USERNAME_NOT_FOUND, userName + "이 없습니다."));

        // password 가 틀림
        if (!encoder.matches(password, selectedUser.getPassword())) {
            throw new AppException(ErrorCode.INVALID_PASSWORD, "패스워드를 잘못 입력했습니다.");
        }
        
        // exception이 발생하지 않았다면 토큰 발행
        String token = JwtTokenUtil.createToken(selectedUser.getUserName(), key, 500l);
        return token;
    }
}
