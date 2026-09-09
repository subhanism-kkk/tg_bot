package az.ingress.tgbot.mapper;

import az.ingress.tgbot.dto.adminUser.AdminUserCreateRequest;
import az.ingress.tgbot.dto.adminUser.AdminUserResponse;
import az.ingress.tgbot.dto.adminUser.AdminUserUpdateRequest;
import az.ingress.tgbot.entity.AdminUser;
import org.mapstruct.*;

@Mapper(componentModel = "spring", unmappedTargetPolicy = ReportingPolicy.IGNORE)
public interface AdminUserMapper {

    AdminUserResponse toResponse(AdminUser entity);

    @Mapping(target = "id", ignore = true)
    AdminUser toEntity(AdminUserCreateRequest request);

    @BeanMapping(nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE)
    @Mapping(target = "id", ignore = true)
    void updateEntity(@MappingTarget AdminUser entity, AdminUserUpdateRequest request);
}