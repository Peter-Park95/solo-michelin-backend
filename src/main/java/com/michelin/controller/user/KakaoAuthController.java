package com.michelin.controller.user;

import com.michelin.service.user.KakaoAuthService;
import lombok.RequiredArgsConstructor;

import jakarta.servlet.http.HttpServletResponse;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.web.bind.annotation.*;

import java.io.IOException;

@RestController
@RequiredArgsConstructor
public class KakaoAuthController {

    private final KakaoAuthService kakaoAuthService;

    @Value("${kakao.rest.api.key}")
    private String KAKAO_CLIENT_ID;

    private final String KAKAO_REDIRECT_URI = "http://localhost:8080/api/auth/kakao/callback";
    private final String FRONTEND_REDIRECT_URI = "http://localhost:5173/kakao-redirect"; // ✅ React 쪽 페이지

    // ✅ 1. 카카오 로그인 페이지로 이동
    @GetMapping("/api/auth/kakao")
    public void redirectToKakao(HttpServletResponse response) throws IOException {
        String kakaoAuthUrl = "https://kauth.kakao.com/oauth/authorize"
                + "?response_type=code"
                + "&client_id=" + KAKAO_CLIENT_ID
                + "&redirect_uri=" + KAKAO_REDIRECT_URI;

        response.sendRedirect(kakaoAuthUrl);
    }

    // ✅ 2. 로그인 완료 후 JWT 발급 → React 쪽으로 리디렉트
    @GetMapping("/api/auth/kakao/callback")
    public void kakaoCallback(@RequestParam String code, HttpServletResponse response) throws IOException {
        String jwt = kakaoAuthService.kakaoLogin(code);
        response.sendRedirect(FRONTEND_REDIRECT_URI + "?token=" + jwt);
    }
}
