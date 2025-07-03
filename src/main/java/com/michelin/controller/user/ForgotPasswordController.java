package com.michelin.controller.user;

import com.michelin.dto.user.ResetPasswordRequest;
import com.michelin.service.user.UserService;
import lombok.RequiredArgsConstructor;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import java.util.Map;


@RestController
@RequestMapping("/api/auth")
@RequiredArgsConstructor
public class ForgotPasswordController {
	// 추후 이메일 인증, SMS 인증, 비밀번호 강도 체크 등 추가 가능한 컨트롤러
	
    private final UserService userService;
    

    //이메일 찾기 (이름 + 폰번호)
    @PostMapping("/find-email")
    public ResponseEntity<?> findEmail1(@RequestBody Map<String, String> request) {
        String username = request.get("username");
        String phone_number = request.get("phone");
        String email = userService.findEmailByUsernameAndPhone(username, phone_number);
        return ResponseEntity.ok(Map.of("email", email));
    }

    //비밀번호 찾기
    @PostMapping("/forgot-password")
    public ResponseEntity<?> forgotPassword(@RequestBody Map<String, String> request) {
        String email = request.get("email");
        userService.processForgotPassword(email);
        return ResponseEntity.ok("비밀번호 재설정 메일을 보냈습니다.");
    }
    
    @PostMapping("/reset-password")
    public ResponseEntity<?> resetPassword(@RequestBody ResetPasswordRequest request) {
        userService.resetPassword(request.getToken(), request.getNewPassword());
        return ResponseEntity.ok("비밀번호가 성공적으로 변경되었습니다.");
    }
}
