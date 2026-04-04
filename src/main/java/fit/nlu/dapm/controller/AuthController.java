package fit.nlu.dapm.controller;

import fit.nlu.dapm.dto.ApiResponse;
import fit.nlu.dapm.dto.auth.*;
import fit.nlu.dapm.exception.BadRequestException;
import fit.nlu.dapm.security.SecurityUtil;
import fit.nlu.dapm.service.AuthService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Map;

@RestController
@RequestMapping("/auth")
public class AuthController {

    @Autowired
    private AuthService authService;

    @Autowired
    private SecurityUtil securityUtil;

    @PostMapping("/login")
    public ResponseEntity<ApiResponse<AuthResponse>> login(@Valid @RequestBody LoginRequest loginRequest) {
        AuthResponse response = authService.login(loginRequest);
        return ResponseEntity.ok(ApiResponse.success("Login successful", response));
    }

    @PostMapping("/register")
    public ResponseEntity<ApiResponse<Void>> register(@Valid @RequestBody RegisterRequest registerRequest) {
        authService.register(registerRequest);
        return new ResponseEntity<>(ApiResponse.success("Registration successful", null), HttpStatus.CREATED);
    }

    @PostMapping("/forgot-password")
    public ResponseEntity<ApiResponse<Void>> forgotPassword(@RequestBody ForgotPasswordRequest request) {
        authService.sendOTP(request.getEmail());
        return ResponseEntity.ok(ApiResponse.success("OTP sent to email", null));
    }

    @PostMapping("/verify-otp")
    public ResponseEntity<ApiResponse<Void>> verifyOTP(@RequestBody VerifyOTPRequest request) {
        authService.verifyOTP(request.getEmail(), request.getOtp());
        return ResponseEntity.ok(ApiResponse.success("OTP valid", null));
    }

    @PostMapping("/change-password")
    public ResponseEntity<ApiResponse<Void>> changePassword(@Valid @RequestBody ChangePasswordRequest request) {
        Long userId = securityUtil.getCurrentUserId();
        if (userId == null) {
            throw new BadRequestException("Unauthorized");
        }

        authService.changePassword(userId, request);
        return ResponseEntity.ok(ApiResponse.success("Password changed successfully", null));
    }

    @PostMapping("/reset-password")
    public ResponseEntity<?> reset(@RequestBody ResetPasswordRequest req) {
        authService.resetPassword(
                req.getEmail(),
                req.getOtp(),
                req.getNewPassword()
        );
        return ResponseEntity.ok(ApiResponse.success("Đổi mật khẩu thành công", null));
    }
}
