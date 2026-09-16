package az.ingress.tgbot.service.impl;

import az.ingress.tgbot.dto.auth.AuthResponse;
import az.ingress.tgbot.dto.auth.LoginRequest;
import az.ingress.tgbot.dto.auth.RefreshTokenRequest;
import az.ingress.tgbot.entity.RegisteredUser;
import az.ingress.tgbot.exception.InvalidCredentialsException;
import az.ingress.tgbot.repository.RegisteredUserRepository;
import az.ingress.tgbot.security.JwtService;
import az.ingress.tgbot.service.AuthService;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class AuthServiceImpl implements AuthService {

    private final RegisteredUserRepository registeredUserRepository;

    private final PasswordEncoder passwordEncoder;

    private final JwtService jwtService;

    @Override
    @Transactional(readOnly = true)
    public AuthResponse login(LoginRequest request) {

        String username =
                request.getUsername()
                        .trim()
                        .toLowerCase();

        RegisteredUser user =
                registeredUserRepository
                        .findByUsernameIgnoreCase(username)
                        .orElseThrow(() ->
                                new InvalidCredentialsException(
                                        "Invalid username or password."
                                )
                        );

        if (!user.isActive()) {

            throw new InvalidCredentialsException(
                    "user account is disabled."
            );
        }

        if (!passwordEncoder.matches(
                request.getPassword(),
                user.getPasswordHash()
        )) {

            throw new InvalidCredentialsException(
                    "Invalid username or password."
            );
        }

        String accessToken =
                jwtService.generateAccessToken(user);

        String refreshToken =
                jwtService.generateRefreshToken(user);

        return AuthResponse.builder()
                .accessToken(accessToken)
                .refreshToken(refreshToken)
                .tokenType("Bearer")
                .username(user.getUsername())
                .role(user.getRole())
                .build();
    }

    @Override
    @Transactional(readOnly = true)
    public AuthResponse refreshToken(
            RefreshTokenRequest request
    ) {

        String refreshToken =
                request.getRefreshToken();

        if (!jwtService.isTokenValid(refreshToken)
                || !jwtService.isRefreshToken(refreshToken)) {

            throw new InvalidCredentialsException(
                    "Expired or invalid refresh token."
            );
        }

        String username =
                jwtService.extractUsername(refreshToken);

        RegisteredUser user =
                registeredUserRepository
                        .findByUsernameIgnoreCase(username)
                        .orElseThrow(() ->
                                new InvalidCredentialsException(
                                        "Invalid refresh token."
                                )
                        );

        if (!user.isActive()) {

            throw new InvalidCredentialsException(
                    "Admin account is disabled."
            );
        }

        if (!jwtService.isTokenValid(
                refreshToken,
                user
        )) {

            throw new InvalidCredentialsException(
                    "Expired or invalid refresh token."
            );
        }

        String newAccessToken =
                jwtService.generateAccessToken(user);

        return AuthResponse.builder()
                .accessToken(newAccessToken)
                .refreshToken(refreshToken)
                .tokenType("Bearer")
                .username(user.getUsername())
                .role(user.getRole())
                .build();
    }
}