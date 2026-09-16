package az.ingress.tgbot.service.impl;

import az.ingress.tgbot.dto.registeredUser.RegisteredUserCreateRequest;
import az.ingress.tgbot.dto.registeredUser.RegisteredUserResponse;
import az.ingress.tgbot.dto.registeredUser.RegisteredUserUpdateRequest;
import az.ingress.tgbot.entity.RegisteredUser;
import az.ingress.tgbot.exception.ResourceAlreadyExistsException;
import az.ingress.tgbot.exception.ResourceNotFoundException;
import az.ingress.tgbot.mapper.RegisteredUserMapper;
import az.ingress.tgbot.repository.RegisteredUserRepository;
import az.ingress.tgbot.service.RegisteredUserService;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class RegisteredUserServiceImpl implements RegisteredUserService {

    private final RegisteredUserRepository registeredUserRepository;
    private final RegisteredUserMapper registeredUserMapper;
    private final PasswordEncoder passwordEncoder;

    @Override
    @Transactional

    public RegisteredUserResponse create(RegisteredUserCreateRequest request) {
        String username = request.getUsername().trim().toLowerCase();

        if (registeredUserRepository.existsByUsernameIgnoreCase(username)) {
            throw new ResourceAlreadyExistsException("Registered User with username '" + username + "' already exists.");
        }

        RegisteredUser user = registeredUserMapper.toEntity(request);
        user.setUsername(username);
        user.setPasswordHash(passwordEncoder.encode(request.getPassword()));

        RegisteredUser saved = registeredUserRepository.save(user);
        return registeredUserMapper.toResponse(saved);
    }

    @Override
    @Transactional
    public RegisteredUserResponse update(Long id, RegisteredUserUpdateRequest request) {

        RegisteredUser entity = registeredUserRepository.findById(id)
                .orElseThrow(() ->
                        new ResourceNotFoundException(
                                "Registered User not found with id: " + id)
                );

        if (request.getUsername() != null && !request.getUsername().isBlank()) {

            String username = request.getUsername().trim().toLowerCase();

            if (registeredUserRepository.existsByUsernameIgnoreCaseAndIdNot(username, id)) {
                throw new ResourceAlreadyExistsException(
                        "Registered User with username '" + username + "' already exists."
                );
            }

            entity.setUsername(username);
        }

        registeredUserMapper.updateEntity(entity, request);

        if (request.getPassword() != null && !request.getPassword().isBlank()) {
            entity.setPasswordHash(
                    passwordEncoder.encode(request.getPassword())
            );
        }

        return registeredUserMapper.toResponse(entity);
    }

    @Override
    public RegisteredUserResponse getById(Long id) {
        RegisteredUser entity = registeredUserRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Registered User not found with id: " + id));
        return registeredUserMapper.toResponse(entity);
    }

    @Override
    public List<RegisteredUserResponse> getAll() {
        return registeredUserRepository.findAll().stream()
                .map(registeredUserMapper::toResponse)
                .toList();
    }

    @Override
    @Transactional
    public void delete(Long id) {
        if (!registeredUserRepository.existsById(id)) {
            throw new ResourceNotFoundException("Registered User not found with id: " + id);
        }
        registeredUserRepository.deleteById(id);
    }
}