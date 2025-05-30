package com.michelin.dto.user;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class UserUpdateRequest {

    private String username;
    private String email;
    private String region;
    private String introduction;

}


