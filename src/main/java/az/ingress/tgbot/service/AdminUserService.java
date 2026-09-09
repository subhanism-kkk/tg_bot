package az.ingress.tgbot.service;

import az.ingress.tgbot.dto.adminUser.AdminUserCreateRequest;
import az.ingress.tgbot.dto.adminUser.AdminUserResponse;
import az.ingress.tgbot.dto.adminUser.AdminUserUpdateRequest;

import java.util.List;

public interface AdminUserService {
    AdminUserResponse create(AdminUserCreateRequest request);
    AdminUserResponse update(Long id, AdminUserUpdateRequest request);
    AdminUserResponse getById(Long id);
    List<AdminUserResponse> getAll();
    void delete(Long id);
}