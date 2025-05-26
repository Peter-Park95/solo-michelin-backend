package com.michelin.service.user;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.michelin.dto.user.KakaoUserRequest;
import com.michelin.dto.user.UserRequest;
import com.michelin.dto.user.UserResponse;
import com.michelin.entity.user.User;
import com.michelin.repository.user.UserRepository;
import com.michelin.util.JwtUtil;

import lombok.RequiredArgsConstructor;

import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.client.RestClientException;
import org.springframework.web.client.RestTemplate;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class KakaoAuthServiceImpl implements KakaoAuthService {

    private final UserRepository userRepository;
    private final JwtUtil jwtUtil;
    private final PasswordEncoder passwordEncoder;

    private final String KAKAO_CLIENT_ID = "192c4f69b1dba52aa44521dbe19751ed"; // 실제 키로 바꾸세용
    private final String KAKAO_REDIRECT_URI = "http://localhost:8080/api/auth/kakao/callback";

    @Override
    @Transactional
    public String kakaoLogin(String code) {
        // 1. Access Token 요청
        String tokenUri = "https://kauth.kakao.com/oauth/token"
                + "?grant_type=authorization_code"
                + "&client_id=" + KAKAO_CLIENT_ID
                + "&redirect_uri=" + KAKAO_REDIRECT_URI
                + "&code=" + code;

        RestTemplate restTemplate = new RestTemplate();
        JsonNode tokenNode = null;
        try {
            tokenNode = new ObjectMapper().readTree(restTemplate.postForObject(tokenUri, null, String.class));
        } catch (JsonProcessingException | RestClientException e) {
            throw new RuntimeException("카카오 토큰 요청 중 오류 발생 (tokenNode)", e);
        }
        String accessToken = tokenNode.get("access_token").asText();

        // 2. 사용자 정보 요청
        String userInfoUri = "https://kapi.kakao.com/v2/user/me";
        org.springframework.http.HttpHeaders headers = new org.springframework.http.HttpHeaders();
        headers.set("Authorization", "Bearer " + accessToken);

        org.springframework.http.HttpEntity<String> entity = new org.springframework.http.HttpEntity<>(headers);
        String userInfoJson = restTemplate.postForObject(userInfoUri, entity, String.class);

        JsonNode userNode = null;
        try {
            userNode = new ObjectMapper().readTree(userInfoJson);
        } catch (JsonProcessingException e) {
            throw new RuntimeException("카카오 토큰 요청 중 오류 발생 (userNode)", e);
        }

        String email = userNode.path("kakao_account").path("email").asText();
        String nickname = userNode.path("properties").path("nickname").asText();

        // 3. DB 저장 or 조회
        Optional<User> userOpt = userRepository.findAll().stream()
                .filter(u -> u.getEmail().equals(email))
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
            userRepository.save(user);
        }

        // 4. JWT 발급
        return jwtUtil.createToken(user.getUsername());
    }
}