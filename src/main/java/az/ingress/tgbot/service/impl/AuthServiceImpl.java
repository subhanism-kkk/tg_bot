package az.ingress.tgbot.service.impl;

import az.ingress.tgbot.dto.auth.AuthResponse;
import az.ingress.tgbot.dto.auth.LoginRequest;
import az.ingress.tgbot.dto.auth.RefreshTokenRequest;
import az.ingress.tgbot.entity.AdminUser;
import az.ingress.tgbot.exception.InvalidCredentialsException;
import az.ingress.tgbot.repository.AdminUserRepository;
import az.ingress.tgbot.security.JwtService;
import az.ingress.tgbot.service.AuthService;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class AuthServiceImpl implements AuthService {

    private final AdminUserRepository adminUserRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtService jwtService;

    @Override
    public AuthResponse login(LoginRequest request) {

        String username = request.getUsername()
                .trim()
                .toLowerCase();

        AdminUser admin = adminUserRepository
                .findByUsernameIgnoreCase(username)
                .orElseThrow(() ->
                        new InvalidCredentialsException(
                                "Invalid username or password."
                        )
                );

        if (!admin.isActive()) {
            throw new InvalidCredentialsException(
                    "Admin account is disabled."
            );
        }

        if (!passwordEncoder.matches(
                request.getPassword(),
                admin.getPasswordHash()
        )) {
            throw new InvalidCredentialsException(
                    "Invalid username or password."
            );
        }

        String accessToken =
                jwtService.generateAccessToken(admin);

        String refreshToken =
                jwtService.generateRefreshToken(admin);

        return AuthResponse.builder()
                .accessToken(accessToken)
                .refreshToken(refreshToken)
                .username(admin.getUsername())
                .role(admin.getRole())
                .build();
    }

    @Override
    public AuthResponse refreshToken(RefreshTokenRequest request) {

        String refreshToken = request.getRefreshToken();

        if (!jwtService.isTokenValid(refreshToken)) {
            throw new InvalidCredentialsException(
                    "Expired or invalid refresh token."
            );
        }

        String username =
                jwtService.extractUsername(refreshToken);

        AdminUser admin = adminUserRepository
                .findByUsernameIgnoreCase(username)
                .orElseThrow(() ->
                        new InvalidCredentialsException(
                                "Invalid refresh token."
                        )
                );

        if (!admin.isActive()) {
            throw new InvalidCredentialsException(
                    "Admin account is disabled."
            );
        }

        if (!jwtService.isTokenValid(refreshToken, admin)) {
            throw new InvalidCredentialsException(
                    "Expired or invalid refresh token."
            );
        }

        String newAccessToken =
                jwtService.generateAccessToken(admin);

        return AuthResponse.builder()
                .accessToken(newAccessToken)
                .refreshToken(refreshToken)
                .username(admin.getUsername())
                .role(admin.getRole())
                .build();
    }
}