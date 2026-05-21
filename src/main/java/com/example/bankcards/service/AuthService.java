package com.example.bankcards.service;

import com.example.bankcards.dto.AuthRequest;
import com.example.bankcards.dto.AuthResponse;
import com.example.bankcards.entity.User;
import com.example.bankcards.exception.BadRequestException;
import com.example.bankcards.repository.UserRepository;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.util.Map;
import static com.example.bankcards.util.Role.*;

@Service
public class AuthService {
    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtService jwtService;
    private final AuthenticationManager authenticationManager;
    private final UserDetailsService userDetailsService;

    public AuthService(UserRepository userRepository, PasswordEncoder passwordEncoder,
                       JwtService jwtService, AuthenticationManager authenticationManager,
                       UserDetailsService userDetailsService) {
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
        this.jwtService = jwtService;
        this.authenticationManager = authenticationManager;
        this.userDetailsService = userDetailsService;
    }

    @Transactional
    public AuthResponse authenticate(AuthRequest request) {

        User user = userRepository.findByUsername(request.username())
                .orElseThrow(() -> new BadRequestException("Неверное имя пользователя или пароль"));

        try {
            authenticationManager.authenticate(
                    new UsernamePasswordAuthenticationToken(request.username(), request.password())
            );
        } catch (BadCredentialsException e) {
            throw new BadRequestException("Неверное имя пользователя или пароль");
        } catch (AuthenticationException e) {
            throw new BadRequestException("Ошибка аутентификации: " + e.getMessage());
        }

        UserDetails userDetails = userDetailsService.loadUserByUsername(user.getUsername());

        String priorityRole = user.getRoles().contains(ROLE_ADMIN)
                ? ROLE_ADMIN.name()
                : ROLE_USER.name();

        String jwtToken = jwtService.generateToken(userDetails, Map.of("roles", priorityRole));

        return new AuthResponse(user.getUsername(), jwtToken, priorityRole);
    }
}
