package az.ingress.tgbot.service.impl;

import az.ingress.tgbot.dto.adminUser.AdminUserCreateRequest;
import az.ingress.tgbot.dto.adminUser.AdminUserResponse;
import az.ingress.tgbot.dto.adminUser.AdminUserUpdateRequest;
import az.ingress.tgbot.entity.AdminUser;
import az.ingress.tgbot.exception.ResourceAlreadyExistsException;
import az.ingress.tgbot.exception.ResourceNotFoundException;
import az.ingress.tgbot.mapper.AdminUserMapper;
import az.ingress.tgbot.repository.AdminUserRepository;
import az.ingress.tgbot.service.AdminUserService;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class AdminUserServiceImpl implements AdminUserService {

    private final AdminUserRepository adminUserRepository;
    private final AdminUserMapper adminUserMapper;
    private final PasswordEncoder passwordEncoder;

    @Override
    @Transactional

    public AdminUserResponse create(AdminUserCreateRequest request) {
        String username = request.getUsername().trim().toLowerCase();

        if (adminUserRepository.existsByUsernameIgnoreCase(username)) {
            throw new ResourceAlreadyExistsException("Admin user with username '" + username + "' already exists.");
        }

        AdminUser user = adminUserMapper.toEntity(request);
        user.setUsername(username);
        user.setPasswordHash(passwordEncoder.encode(request.getPassword()));

        AdminUser saved = adminUserRepository.save(user);
        return adminUserMapper.toResponse(saved);
    }

    @Override
    @Transactional
    public AdminUserResponse update(Long id, AdminUserUpdateRequest request) {

        AdminUser entity = adminUserRepository.findById(id)
                .orElseThrow(() ->
                        new ResourceNotFoundException(
                                "Admin user not found with id: " + id)
                );

        String username = request.getUsername().trim().toLowerCase();

        if (adminUserRepository.existsByUsernameIgnoreCaseAndIdNot(username, id)) {
            throw new ResourceAlreadyExistsException(
                    "Admin user with username '" + username + "' already exists.");
        }

        adminUserMapper.updateEntity(entity, request);
        entity.setUsername(username);

        if (request.getPassword() != null && !request.getPassword().isBlank()) {
            entity.setPasswordHash(
                    passwordEncoder.encode(request.getPassword()));
        }
        return adminUserMapper.toResponse(entity);
    }

    @Override
    public AdminUserResponse getById(Long id) {
        AdminUser entity = adminUserRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Admin user not found with id: " + id));
        return adminUserMapper.toResponse(entity);
    }

    @Override
    public List<AdminUserResponse> getAll() {
        return adminUserRepository.findAll().stream()
                .map(adminUserMapper::toResponse)
                .toList();
    }

    @Override
    @Transactional
    public void delete(Long id) {
        if (!adminUserRepository.existsById(id)) {
            throw new ResourceNotFoundException("Admin user not found with id: " + id);
        }
        adminUserRepository.deleteById(id);
    }
}