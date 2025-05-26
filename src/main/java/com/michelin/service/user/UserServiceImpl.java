package com.michelin.service.user;

import com.michelin.dto.user.LoginRequest;
import com.michelin.dto.user.UserRequest;
import com.michelin.dto.user.UserResponse;
import com.michelin.dto.user.UserUpdateRequest;
import com.michelin.entity.user.User;
import com.michelin.repository.user.UserRepository;
import com.michelin.util.JwtUtil;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class UserServiceImpl implements UserService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;

    private final JwtUtil jwtUtil;

    @Override
    @Transactional
    public UserResponse createUser(UserRequest request){
        User user = User.builder()
                .username(request.getUsername())
                .email(request.getEmail())
                .password(passwordEncoder.encode(request.getPassword()))
                .created(LocalDateTime.now())
                .deleted(false)
                .build();
        User savedUser = userRepository.save(user);
        return UserResponse.from(savedUser);
    }

    @Override
    public Page<UserResponse> getAllUsers(Pageable pageable) {
        return userRepository.findByDeletedFalse(pageable)
                .map(UserResponse::from);
    }
    @Override
    public UserResponse getUserById(Long id){
        User user = userRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("사용자를 찾을 수 없습니다."));
        return UserResponse.from(user);

    }
    @Override
    @Transactional
    public UserResponse updateUser(Long id, UserUpdateRequest request){
        User user = userRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("사용자를 찾을 수 없습니다."));

        user = User.builder()
                .id(user.getId())
                .username(request.getUsername())
                .email(request.getEmail())
                .password(user.getPassword())
                .profileImage(request.getProfileImage())
                .region(request.getRegion())
                .introduction(request.getIntroduction())
                .created(user.getCreated())
                .deleted(user.isDeleted())
                .build();
        User updated = userRepository.save(user);
        return UserResponse.from(updated);
    }
    @Override
    @Transactional
    public void deleteUser(Long id){
        User user = userRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("사용자를 찾을 수 없습니다."));
        user = User.builder()
                .id(user.getId())
                .username(user.getUsername())
                .email(user.getEmail())
                .password(user.getPassword())
                .created(user.getCreated())
                .deleted(true)
                .build();
        userRepository.save(user);
    }
    //로그인
    @Override
    public String login(LoginRequest request) {
        User user = userRepository.findByEmail(request.getEmail())
                .orElseThrow(() -> new RuntimeException("존재하지 않는 이메일입니다"));
        if (!passwordEncoder.matches(request.getPassword(), user.getPassword())) {
            throw new RuntimeException("비밀번호가 틀렸습니다");
        }
        return jwtUtil.createToken(user.getEmail(), user.getId());
    }
}
