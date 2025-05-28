package com.michelin.service.user;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.michelin.entity.user.User;
import com.michelin.repository.user.UserRepository;
import com.michelin.util.JwtUtil;

import lombok.RequiredArgsConstructor;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.*;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.client.RestClientException;
import org.springframework.web.client.RestTemplate;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;

import java.time.LocalDateTime;
import java.util.Optional;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class KakaoAuthServiceImpl implements KakaoAuthService {

    private final UserRepository userRepository;
    private final JwtUtil jwtUtil;
    private final PasswordEncoder passwordEncoder;

    @Value("${kakao.rest.api.key}")
    private String KAKAO_CLIENT_ID;

    private final String KAKAO_REDIRECT_URI = "http://localhost:8080/api/auth/kakao/callback";

    @Override
    @Transactional
    public String kakaoLogin(String code) {
        // 1. Access Token 요청
        String tokenUri = "https://kauth.kakao.com/oauth/token";

        HttpHeaders tokenHeaders = new HttpHeaders();
        tokenHeaders.setContentType(MediaType.APPLICATION_FORM_URLENCODED);

        MultiValueMap<String, String> params = new LinkedMultiValueMap<>();
        params.add("grant_type", "authorization_code");
        params.add("client_id", KAKAO_CLIENT_ID);
        params.add("redirect_uri", KAKAO_REDIRECT_URI);
        params.add("code", code);

        HttpEntity<MultiValueMap<String, String>> tokenRequest = new HttpEntity<>(params, tokenHeaders);

        RestTemplate restTemplate = new RestTemplate();
        JsonNode tokenNode;
        try {
            String response = restTemplate.postForObject(tokenUri, tokenRequest, String.class);
            tokenNode = new ObjectMapper().readTree(response);
        } catch (JsonProcessingException | RestClientException e) {
            throw new RuntimeException("카카오 토큰 요청 중 오류 발생 (tokenNode)", e);
        }

        String accessToken = tokenNode.get("access_token").asText();

        // 2. 사용자 정보 요청
        String userInfoUri = "https://kapi.kakao.com/v2/user/me";
        HttpHeaders userInfoHeaders = new HttpHeaders();
        userInfoHeaders.set("Authorization", "Bearer " + accessToken);

        HttpEntity<String> userInfoRequest = new HttpEntity<>(userInfoHeaders);
        String userInfoJson = restTemplate.postForObject(userInfoUri, userInfoRequest, String.class);

        JsonNode userNode;
        try {
            userNode = new ObjectMapper().readTree(userInfoJson);
        } catch (JsonProcessingException e) {
            throw new RuntimeException("카카오 사용자 정보 파싱 오류 (userNode)", e);
        }

        String email = userNode.path("kakao_account").path("email").asText();
        String nickname = userNode.path("properties").path("nickname").asText();
        String profileImageUrl = userNode
                .path("kakao_account")
                .path("profile")
                .path("profile_image_url")
                .asText();
        System.out.println("🔍 카카오 사용자 정보 JSON: " + profileImageUrl);
        // 3. DB 저장 or 조회
        Optional<User> userOpt = userRepository.findAll().stream()
                .filter(u -> u.getEmail().equals(email) && !u.isDeleted())  // (soft delete)
                .findFirst();
        User user;
        if (userOpt.isPresent()) {
            user = userOpt.get();
        } else {
            String dummyPassword = passwordEncoder.encode(UUID.randomUUID().toString());
            user = new User();
            user.setUsername(nickname);
            user.setEmail(email);
            user.setPassword(dummyPassword);
            user.setProfileImageUrl(profileImageUrl);
            user.setCreated(LocalDateTime.now());
            userRepository.save(user);
        }

        // 4. JWT 발급
        return jwtUtil.createToken(user.getUsername(), user.getId());
    }
}
