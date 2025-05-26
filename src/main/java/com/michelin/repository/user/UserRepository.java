package com.michelin.repository.user;

import com.michelin.entity.user.User;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface UserRepository extends JpaRepository<User, Long> {
    // 기본 CRUD 제공 (findAll, findById, save, deleteById 등)
    Page<User> findByDeletedFalse(Pageable pageable);
    //login
    Optional<User> findByEmail(String email);

    boolean existsByEmail(@NotBlank(message = " 이메일은 필수입니다.") @Email(message = "이메일 형식이 올바르지 않습니다.") String email);
}
