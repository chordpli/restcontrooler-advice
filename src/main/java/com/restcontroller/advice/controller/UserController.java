package com.restcontroller.advice.controller;

import com.restcontroller.advice.domain.Response;
import com.restcontroller.advice.domain.User;
import com.restcontroller.advice.domain.dto.UserJoinRequest;
import com.restcontroller.advice.domain.dto.UserJoinResponse;
import com.restcontroller.advice.service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1/users")
public class UserController {

    private final UserService userService;

    @PostMapping("/join")
    public Response<UserJoinResponse> join(@RequestBody UserJoinRequest dto) {
        User user = userService.join(dto);
        return Response.success(new UserJoinResponse());
    }
}
