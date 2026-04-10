package fit.nlu.dapm.service;

import fit.nlu.dapm.dto.auth.AuthResponse;
import fit.nlu.dapm.dto.auth.LoginRequest;
import fit.nlu.dapm.dto.auth.RegisterRequest;
import fit.nlu.dapm.entity.PasswordResetOTP;
import fit.nlu.dapm.entity.Role;
import fit.nlu.dapm.entity.User;
import fit.nlu.dapm.exception.BadRequestException;
import fit.nlu.dapm.repository.PasswordResetOTPRepository;
import fit.nlu.dapm.repository.RoleRepository;
import fit.nlu.dapm.repository.UserRepository;
import fit.nlu.dapm.security.JwtTokenProvider;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;

@Service
public class AuthService {

    @Autowired
    private AuthenticationManager authenticationManager;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private RoleRepository roleRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @Autowired
    private JwtTokenProvider tokenProvider;

    @Autowired
    private PasswordResetOTPRepository otpRepository;

    @Autowired
    private EmailService emailService;

    public AuthResponse login(LoginRequest loginRequest) {
        User user = userRepository.findByEmail(loginRequest.getEmail())
                .orElseThrow(() -> new RuntimeException("EMAIL_NOT_FOUND"));

        try {
            Authentication authentication = authenticationManager.authenticate(
                    new UsernamePasswordAuthenticationToken(
                            loginRequest.getEmail(),
                            loginRequest.getPassword()
                    )
            );

            SecurityContextHolder.getContext().setAuthentication(authentication);

            String jwt = tokenProvider.generateToken(authentication);
            return new AuthResponse(jwt);

        } catch (BadCredentialsException ex) {
            throw new RuntimeException("INVALID_PASSWORD");
        }
    }

    @Transactional
    public void register(RegisterRequest registerRequest) {
        if (userRepository.existsByEmail(registerRequest.getEmail())) {
            throw new BadRequestException("Email address already in use.");
        }

        User user = new User();
        user.setFullName(registerRequest.getFullName());
        user.setEmail(registerRequest.getEmail());
        user.setPassword(passwordEncoder.encode(registerRequest.getPassword()));

        Role userRole = roleRepository.findByRoleName("USER").orElseGet(() -> {
            Role newRole = new Role();
            newRole.setRoleName("USER");
            return roleRepository.save(newRole);
        });

        user.setRole(userRole);

        userRepository.save(user);
    }
    // Gửi otp
    @Transactional
    public void sendOTP(String email) {
        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new BadRequestException("Email not found"));

        String otp = String.valueOf((int)(Math.random() * 900000) + 100000);

        otpRepository.deleteByEmail(email);

        PasswordResetOTP otpEntity = new PasswordResetOTP();
        otpEntity.setEmail(email);
        otpEntity.setOtp(otp);
        otpEntity.setExpiryTime(LocalDateTime.now().plusMinutes(5));

        otpRepository.save(otpEntity);

        emailService.sendOTP(email, otp);
    }
    //verify
    @Transactional
    public void verifyOTP(String email, String otp) {
        PasswordResetOTP otpEntity = otpRepository.findByEmailAndOtp(email, otp)
                .orElseThrow(() -> new BadRequestException("Invalid OTP"));

        if (otpEntity.getExpiryTime().isBefore(LocalDateTime.now())) {
            throw new BadRequestException("OTP expired");
        }
    }
}

