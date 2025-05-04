package org.schoolproject.backend.repositories;

import org.schoolproject.backend.entities.VerificationCode;
import org.springframework.data.jpa.repository.JpaRepository;

public interface VerificationCodeRepository extends JpaRepository<VerificationCode, Integer> {
}
