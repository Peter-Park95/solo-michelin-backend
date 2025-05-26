package com.michelin.dto.user;


import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class KakaoUserRequest {

    private String email;
    private String nickname;
    private String username;

}