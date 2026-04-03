package fit.nlu.dapm.repository;

import fit.nlu.dapm.entity.PasswordResetOTP;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface PasswordResetOTPRepository extends JpaRepository<PasswordResetOTP, Long> {
    Optional<PasswordResetOTP> findByEmailAndOtp(String email, String otp);
    void deleteByEmail(String email);
}