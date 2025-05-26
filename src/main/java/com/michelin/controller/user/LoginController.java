package com.michelin.controller.user;



import com.michelin.dto.user.LoginRequest;
import com.michelin.service.user.UserService;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api")
public class LoginController {

    private final UserService userService;

    public LoginController(UserService userService){
        this.userService = userService;
    }

    @PostMapping("/login")
    public ResponseEntity<?> login(@RequestBody LoginRequest request) {
        String jwt = userService.login(request); // JWT 반환
        return ResponseEntity.ok(jwt);
    }
}