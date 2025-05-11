package org.schoolproject.backend.services;

public interface VerificationCodeService {

    void generateAndSendCode(String email);
    void resendCode(String email);
    void verifyCode(String email, String code);
    void cleanExpiredCode();

}
