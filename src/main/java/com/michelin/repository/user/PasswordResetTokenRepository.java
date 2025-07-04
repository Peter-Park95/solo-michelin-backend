package com.michelin.repository.user;

import com.michelin.entity.user.PasswordResetToken;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface PasswordResetTokenRepository extends JpaRepository<PasswordResetToken, Long> {
	
    Optional<PasswordResetToken> findByToken(String token);
    
}
