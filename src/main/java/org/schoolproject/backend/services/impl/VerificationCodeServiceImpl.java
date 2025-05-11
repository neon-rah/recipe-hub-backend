package org.schoolproject.backend.services.impl;

import lombok.RequiredArgsConstructor;
import org.schoolproject.backend.entities.VerificationCode;
import org.schoolproject.backend.repositories.VerificationCodeRepository;
import org.schoolproject.backend.services.EmailService;
import org.schoolproject.backend.services.VerificationCodeService;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.security.SecureRandom;
import java.time.LocalDateTime;

@Service
@RequiredArgsConstructor
public class VerificationCodeServiceImpl implements VerificationCodeService {

    private final VerificationCodeRepository verificationCodeRepository;

    private final EmailService emailService;

    private static final int CODE_LENGTH = 6;
    private static final int EXPIRY_MINUTES = 10;

    /**
     * @param email
     */
    @Override
    @Transactional
    public void generateAndSendCode(String email) {
        // Vérifier si un code existe déjà
        verificationCodeRepository.findByEmail(email).ifPresent(code -> {
            if (code.getExpiryDate().isAfter(LocalDateTime.now())) {
                throw new IllegalArgumentException("An valid verification already exist with this email");
            }
            verificationCodeRepository.deleteByEmail(email);
        });

        // generer le code
        String code = generateVerificationCode();

        // create and save
        VerificationCode verificationCode = VerificationCode.builder()
                .email(email)
                .code(code)
                .createdAt(LocalDateTime.now())
                .expiryDate(LocalDateTime.now().plusMinutes(EXPIRY_MINUTES))
                .build();
        verificationCodeRepository.save(verificationCode);

        //send Email
        emailService.sendEmail(
                email,
                "Verification Code",
                "Your verification code is : " + code + "\n This code is valid  until "+verificationCode.getExpiryDate()
        );
    }

    /**
     * @param email
     */
    @Override
    @Transactional
    public void resendCode(String email) {
        verificationCodeRepository.deleteByEmail(email);
        generateAndSendCode(email);
    }

    /**
     * @param email
     * @param code
     */
    @Override
    @Transactional
    public void verifyCode(String email, String code) {
        VerificationCode verificationCode = verificationCodeRepository.findByEmail(email).orElseThrow(
                () -> new RuntimeException("No verification code found for email " + email)
        );

        if (verificationCode.getExpiryDate().isBefore(LocalDateTime.now())) {
            verificationCodeRepository.deleteByEmail(email);
            throw new IllegalArgumentException("Expired verification code");
        }
        if (!verificationCode.getCode().equals(code)) {
            throw new IllegalArgumentException("Invalid verification code");
        }
    }

    /**
     * Nettoyer les codes expires toutes les heures
     */
    @Override
    @Scheduled(fixedRate = 60*60*1000)    // toutes les heures
    @Transactional
    public void cleanExpiredCode() {
        verificationCodeRepository.deleteByExpiryDateBefore(LocalDateTime.now());
    }

    /*
    * Générer un code aléatoire.
     */
    private String generateVerificationCode() {
        SecureRandom random = new SecureRandom();
        StringBuilder code = new StringBuilder();
        for (int i = 0; i < CODE_LENGTH; i++) {
            code.append(random.nextInt(10));
        }
        return code.toString();
    }

}
