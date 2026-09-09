//package az.ingress.tgbot.service.impl;
//
//import az.ingress.tgbot.dto.adminUser.AdminUserResponse;
//import az.ingress.tgbot.dto.auth.AuthResponse;
//import az.ingress.tgbot.dto.auth.LoginRequest;
//import az.ingress.tgbot.dto.auth.RefreshTokenRequest;
//import az.ingress.tgbot.dto.auth.RegisterRequest;
//import az.ingress.tgbot.entity.AdminUser;
//import az.ingress.tgbot.enums.AdminRole;
//import az.ingress.tgbot.exception.InvalidCredentialsException;
//import az.ingress.tgbot.exception.ResourceAlreadyExistsException;
//import az.ingress.tgbot.mapper.AdminUserMapper;
//import az.ingress.tgbot.repository.AdminUserRepository;
//import az.ingress.tgbot.security.JwtService;
//import az.ingress.tgbot.service.AuthService;
//import lombok.RequiredArgsConstructor;
//import org.springframework.security.crypto.password.PasswordEncoder;
//import org.springframework.stereotype.Service;
//import org.springframework.transaction.annotation.Transactional;
//
//@Service
//@RequiredArgsConstructor;
//public class AuthServiceImpl implements AuthService {
//
//    private final AdminUserRepository adminUserRepository;
//    private final PasswordEncoder passwordEncoder;
//    private final JwtService jwtService;
//    private final AdminUserMapper adminUserMapper;
//
//    @Override
//    @Transactional
//    public AdminUserResponse register(RegisterRequest request) {
//        String username = request.getUsername().trim().toLowerCase();
//
//        if (adminUserRepository.existsByUsernameIgnoreCase(username)) {
//            throw new ResourceAlreadyExistsException("Admin user with username '" + username + "' already exists.");
//        }
//
//        AdminUser adminUser = AdminUser.builder()
//                .username(username)
//                .passwordHash(passwordEncoder.encode(request.getPassword()))
//                .role(request.getRole() != null ? request.getRole() : AdminRole.ADMIN)
//                .build();
//
//        AdminUser savedUser = adminUserRepository.save(adminUser);
//        return adminUserMapper.toResponse(savedUser);
//    }
//
//    @Override
//    public AuthResponse login(LoginRequest request) {
//        String username = request.getUsername().trim().toLowerCase();
//
//        AdminUser adminUser = adminUserRepository.findByUsernameIgnoreCase(username)
//                .orElseThrow(() -> new InvalidCredentialsException("Invalid username or password."));
//
//        if (!passwordEncoder.matches(request.getPassword(), adminUser.getPasswordHash())) {
//            throw new InvalidCredentialsException("Invalid username or password.");
//        }
//
//        String accessToken = jwtService.generateAccessToken(adminUser);
//        String refreshToken = jwtService.generateRefreshToken(adminUser);
//
//        return AuthResponse.builder()
//                .accessToken(accessToken)
//                .refreshToken(refreshToken)
//                .tokenType("Bearer")
//                .build();
//    }
//
//    @Override
//    public AuthResponse refreshToken(RefreshTokenRequest request) {
//        String username = jwtService.extractUsername(request.getRefreshToken());
//
//        AdminUser adminUser = adminUserRepository.findByUsernameIgnoreCase(username)
//                .orElseThrow(() -> new InvalidCredentialsException("Invalid refresh token."));
//
//        if (!jwtService.isTokenValid(request.getRefreshToken(), adminUser)) {
//            throw new InvalidCredentialsException("Expired or invalid refresh token.");
//        }
//
//        String newAccessToken = jwtService.generateAccessToken(adminUser);
//
//        return AuthResponse.builder()
//                .accessToken(newAccessToken)
//                .refreshToken(request.getRefreshToken())
//                .tokenType("Bearer")
//                .build();
//    }
//}