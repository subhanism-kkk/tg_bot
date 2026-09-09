package az.ingress.tgbot.service;

import az.ingress.tgbot.dto.adminUser.AdminUserResponse;
import az.ingress.tgbot.dto.auth.AuthResponse;
import az.ingress.tgbot.dto.auth.LoginRequest;
import az.ingress.tgbot.dto.auth.RefreshTokenRequest;
import az.ingress.tgbot.dto.auth.RegisterRequest;

public interface AuthService {

    AuthResponse login(LoginRequest request);

    AdminUserResponse register(RegisterRequest request);

    AuthResponse refreshToken(RefreshTokenRequest request);
}