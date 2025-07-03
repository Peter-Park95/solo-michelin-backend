package com.michelin.service.user;

import com.michelin.dto.user.LoginRequest;
import com.michelin.dto.user.UserRequest;
import com.michelin.dto.user.UserResponse;
import com.michelin.dto.user.UserUpdateRequest;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.web.multipart.MultipartFile;


public interface UserService {
    UserResponse createUser(UserRequest request);
    
    Page<UserResponse> getAllUsers(Pageable pageable);
    
    UserResponse getUserById(Long id);
    
    UserResponse updateUser(Long id, UserUpdateRequest request, MultipartFile image);
    
    void deleteUser(Long id);
    
    //로그인
    String login(LoginRequest request);

    //이메일 찾기
	String findEmailByUsername(String username);
    String findEmailByUsernameAndPhone(String username, String phone_number);
    //비밀번호 찾기
	void processForgotPassword(String email);
	
	void resetPassword(String token, String newPassword);
	
}
