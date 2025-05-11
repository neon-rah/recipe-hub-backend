package org.schoolproject.backend.repositories;

import org.schoolproject.backend.entities.VerificationCode;
import org.springframework.data.jpa.repository.JpaRepository;

import java.time.LocalDateTime;
import java.util.Optional;

public interface VerificationCodeRepository extends JpaRepository<VerificationCode, Integer> {
    Optional<VerificationCode> findByEmail(String code);
    void deleteByEmail(String email);

    void deleteByExpiryDateBefore(LocalDateTime expiryDateBefore);
}
