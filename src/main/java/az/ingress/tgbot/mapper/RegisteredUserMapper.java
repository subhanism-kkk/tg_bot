package az.ingress.tgbot.mapper;

import az.ingress.tgbot.dto.registeredUser.RegisteredUserCreateRequest;
import az.ingress.tgbot.dto.registeredUser.RegisteredUserResponse;
import az.ingress.tgbot.dto.registeredUser.RegisteredUserUpdateRequest;
import az.ingress.tgbot.entity.RegisteredUser;
import org.mapstruct.*;

@Mapper(componentModel = "spring", unmappedTargetPolicy = ReportingPolicy.IGNORE)
public interface RegisteredUserMapper {

    RegisteredUserResponse toResponse(RegisteredUser entity);

    @Mapping(target = "id", ignore = true)
    RegisteredUser toEntity(RegisteredUserCreateRequest request);

    @BeanMapping(nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE)
    @Mapping(target = "id", ignore = true)
    void updateEntity(@MappingTarget RegisteredUser entity, RegisteredUserUpdateRequest request);
}