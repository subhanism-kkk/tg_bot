package az.ingress.tgbot.service;

import az.ingress.tgbot.dto.registeredUser.RegisteredUserCreateRequest;
import az.ingress.tgbot.dto.registeredUser.RegisteredUserResponse;
import az.ingress.tgbot.dto.registeredUser.RegisteredUserUpdateRequest;

import java.util.List;

public interface RegisteredUserService {
    RegisteredUserResponse create(RegisteredUserCreateRequest request);
    RegisteredUserResponse update(Long id, RegisteredUserUpdateRequest request);
    RegisteredUserResponse getById(Long id);
    List<RegisteredUserResponse> getAll();
    void delete(Long id);
}