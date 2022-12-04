package com.restcontroller.advice.service;

import com.restcontroller.advice.domain.User;
import com.restcontroller.advice.domain.dto.UserDto;
import com.restcontroller.advice.domain.dto.UserJoinRequest;
import com.restcontroller.advice.domain.dto.UserLoginRequest;
import com.restcontroller.advice.repository.UserRepository;
import com.restcontroller.advice.util.JwtTokenUtil;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class UserService {

    private final UserRepository userRepository;
    private final BCryptPasswordEncoder encoder;

    @Value("${jwt.token.secret}")
    private String secretKey;

    private long expireTimeMs = 1000 * 60 * 60;

    public UserDto join(UserJoinRequest dto) {
        User savedUser = userRepository.save(dto.toEntity(encoder.encode(dto.getPassword())));
        return UserDto.builder()
                .userName(savedUser.getUserName())
                .emailAddress(savedUser.getEmailAddress())
                .build();
    }

    public String login(UserLoginRequest dto) {
        // user name 확인
        User user = userRepository.findByUserName(dto.getUserName())
                .orElseThrow(RuntimeException::new);

        // password 일치
        if (!encoder.matches(dto.getPassword(), user.getPassword())) {
            throw new RuntimeException();
        }

        return JwtTokenUtil.createToken(dto.getUserName(), secretKey, expireTimeMs);
    }
}
