package com.restcontroller.advice.domain.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@AllArgsConstructor
@Getter
@NoArgsConstructor
@Builder
public class UserDto {
    private Long id;
    private String userName;
    private String password;
    private String emailAddress;
}
