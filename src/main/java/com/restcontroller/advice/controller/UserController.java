package com.restcontroller.advice.controller;

import com.restcontroller.advice.domain.Response;
import com.restcontroller.advice.domain.User;
import com.restcontroller.advice.domain.dto.*;
import com.restcontroller.advice.service.UserService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1/users")
@Slf4j
public class UserController {

    private final UserService userService;


    @PostMapping("/join")
    public Response<UserJoinResponse> join(@RequestBody UserJoinRequest dto) {
        log.info("join dto : {}", dto);
        UserDto user = userService.join(dto);
        log.info("saved User={}, mail = {}", user.getUserName(), user.getEmailAddress());
        return Response.success(new UserJoinResponse(user.getUserName(), user.getEmailAddress()));
    }

    @PostMapping("/login")
    public Response<UserLoginResponse> login(@RequestBody UserLoginRequest dto) {
        String token = userService.login(dto);
        return Response.success(new UserLoginResponse(token));
    }
}
