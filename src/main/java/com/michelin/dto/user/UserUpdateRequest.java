package com.michelin.dto.user;


import jakarta.validation.constraints.Email;
import lombok.Getter;
import lombok.Setter;
import jakarta.validation.constraints.NotBlank;

@Getter
@Setter
public class UserUpdateRequest {

    @NotBlank(message = "사용자 이름은 필수입니다.")
    private String username;

    @NotBlank(message = " 이메일은 필수입니다.")
    @Email(message = "이메일 형식이 올바르지 않습니다.")
    private String email;


    private String profileImage;
    private String region;
    private String introduction;

}


