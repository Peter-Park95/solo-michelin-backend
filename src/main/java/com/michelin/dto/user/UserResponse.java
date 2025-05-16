package com.michelin.dto.user;

import com.michelin.entity.user.User;
import lombok.Builder;
import lombok.Getter;

import java.time.LocalDateTime;

@Getter
@Builder
public class UserResponse {
    private Long id;
    private  String username;
    private String email;
    private String profileImage;
    private String region;
    private String introduction;
    private LocalDateTime created;

    public static UserResponse from(User user){ // 비밀번호,삭제여부 숨김
        return UserResponse.builder()
                .id(user.getId())
                .username(user.getUsername())
                .email(user.getEmail())
                .profileImage(user.getProfileImage())       
                .region(user.getRegion())
                .introduction(user.getIntroduction())
                .created(user.getCreated())
                .build();
    }
}
