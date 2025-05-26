package com.michelin.service.user;

import com.michelin.dto.user.LoginRequest;
import com.michelin.dto.user.UserRequest;
import com.michelin.dto.user.UserResponse;
import com.michelin.dto.user.UserUpdateRequest;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.List;

public interface UserService {
    UserResponse createUser(UserRequest request);
    Page<UserResponse> getAllUsers(Pageable pageable);
    UserResponse getUserById(Long id);
    UserResponse updateUser(Long id, UserUpdateRequest request);
    void deleteUser(Long id);
    //로그인
    String login(LoginRequest request);
}
