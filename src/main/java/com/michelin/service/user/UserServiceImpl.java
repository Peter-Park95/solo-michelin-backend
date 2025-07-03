package com.michelin.service.user;

import com.michelin.dto.user.LoginRequest;
import com.michelin.dto.user.UserRequest;
import com.michelin.dto.user.UserResponse;
import com.michelin.dto.user.UserUpdateRequest;
import com.michelin.entity.user.PasswordResetToken;
import com.michelin.entity.user.User;
import com.michelin.repository.user.PasswordResetTokenRepository;
import com.michelin.repository.user.UserRepository;
import com.michelin.util.JwtUtil;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;
import com.michelin.service.aws.S3Uploader;

import java.io.IOException;
import java.time.LocalDateTime;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class UserServiceImpl implements UserService {

    private final UserRepository userRepository;
    private final PasswordResetTokenRepository passwordResetTokenRepository;
    private final EmailService emailService;
    
    private final PasswordEncoder passwordEncoder;
    private final S3Uploader s3Uploader;
    private final JwtUtil jwtUtil;

    @Override
    @Transactional
    public UserResponse createUser(UserRequest request){
        if (userRepository.existsByEmail(request.getEmail())) {
            throw new IllegalArgumentException("이미 사용 중인 이메일입니다.");
        }
        User user = User.builder()
                .username(request.getUsername())
                .email(request.getEmail())
                .password(passwordEncoder.encode(request.getPassword()))
                .phoneNumber(request.getPhoneNumber())
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
    @Transactional
    public UserResponse updateUser(Long id, UserUpdateRequest request, MultipartFile image) {
        User user = userRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("사용자를 찾을 수 없습니다."));

        if (request.getUsername() != null) {
            user.setUsername(request.getUsername());
        }

        if (request.getEmail() != null) {
            user.setEmail(request.getEmail());
        }

        if (request.getRegion() != null) {
            user.setRegion(request.getRegion());
        }

        if (request.getIntroduction() != null) {
            user.setIntroduction(request.getIntroduction());
        }

        if (image != null && !image.isEmpty()) {
            try {
                String imageUrl = s3Uploader.upload(image);
                user.setProfileImage(imageUrl);
            } catch (IOException e) {
                throw new RuntimeException("이미지 업로드 중 오류가 발생했습니다.", e);
            }
        }

        return UserResponse.from(userRepository.save(user));
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
    
    //이메일 찾기
    @Override
    public String findEmailByUsername(String username) {
        User user = userRepository.findByUsername(username)
            .orElseThrow(() -> new RuntimeException("해당 닉네임으로 가입된 계정을 찾을 수 없습니다."));
        
        return user.getEmail();
    }

    @Override
    public String findEmailByUsernameAndPhone(String username, String phone_number) {
        User user = userRepository.findByUsernameAndPhoneNumber(username, phone_number)
                .orElseThrow(() -> new RuntimeException("입력하신 정보와 일치하는 계정을 찾을 수 없습니다."));

        return user.getEmail();
    }
    
    //비밀번호 찾기: 이메일 발송
    @Override
    public void processForgotPassword(String email) {
        User user = userRepository.findByEmail(email)
            .orElseThrow(() -> new RuntimeException("해당 이메일의 사용자가 없습니다."));

        String token = UUID.randomUUID().toString();
        PasswordResetToken resetToken = new PasswordResetToken(
            token,
            user,
            LocalDateTime.now().plusMinutes(15)
        );

        passwordResetTokenRepository.save(resetToken);

        // 이메일 전송 (이메일 보내는 로직은 별도 EmailService에서 구현)
        emailService.sendPasswordResetEmail(user.getEmail(), token);
    }
    
  //비밀번호 찾기: 비밀번호 변경
    @Override
    @Transactional
    public void resetPassword(String token, String newPassword) {
        PasswordResetToken resetToken = passwordResetTokenRepository.findByToken(token)
            .orElseThrow(() -> new RuntimeException("유효하지 않은 토큰입니다."));

        if (resetToken.isExpired()) {
            throw new RuntimeException("토큰이 만료되었습니다.");
        }

        User user = resetToken.getUser();
        
        user.setPassword(passwordEncoder.encode(newPassword));  // 암호화
        
        userRepository.save(user);
        passwordResetTokenRepository.delete(resetToken); // 보안: 사용 후 삭제
    }
}
