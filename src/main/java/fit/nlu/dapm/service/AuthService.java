package fit.nlu.dapm.service;

import fit.nlu.dapm.dto.auth.AuthResponse;
import fit.nlu.dapm.dto.auth.LoginRequest;
import fit.nlu.dapm.dto.auth.RegisterRequest;
import fit.nlu.dapm.entity.Role;
import fit.nlu.dapm.entity.User;
import fit.nlu.dapm.exception.BadRequestException;
import fit.nlu.dapm.repository.RoleRepository;
import fit.nlu.dapm.repository.UserRepository;
import fit.nlu.dapm.security.JwtTokenProvider;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

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

    public AuthResponse login(LoginRequest loginRequest) {
        Authentication authentication = authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(
                        loginRequest.getEmail(),
                        loginRequest.getPassword()
                )
        );

        SecurityContextHolder.getContext().setAuthentication(authentication);

        String jwt = tokenProvider.generateToken(authentication);
        return new AuthResponse(jwt);
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
}
