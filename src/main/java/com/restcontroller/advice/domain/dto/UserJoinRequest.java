package com.restcontroller.advice.domain.dto;

import com.restcontroller.advice.domain.User;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

@AllArgsConstructor
@NoArgsConstructor
@Getter
public class UserJoinRequest {

    private String userName;
    private String password;
    private String emailAddress;

    public User toEntity(String password){
        return User.builder()
                .userName(this.userName)
                .password(password)
                .emailAddress(this.emailAddress)
                .build();
    }
}
