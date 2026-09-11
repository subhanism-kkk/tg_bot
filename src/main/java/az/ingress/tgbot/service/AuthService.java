package az.ingress.tgbot.service;

import az.ingress.tgbot.dto.auth.AuthResponse;
import az.ingress.tgbot.dto.auth.LoginRequest;
import az.ingress.tgbot.dto.auth.RefreshTokenRequest;

public interface AuthService {

    AuthResponse login(LoginRequest request);

    AuthResponse refreshToken(RefreshTokenRequest request);
}