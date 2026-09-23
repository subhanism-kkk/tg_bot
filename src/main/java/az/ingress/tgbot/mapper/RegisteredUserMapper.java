package az.ingress.tgbot.mapper;

import az.ingress.tgbot.dto.registeredUser.RegisteredUserCreateRequest;
import az.ingress.tgbot.dto.registeredUser.RegisteredUserResponse;
import az.ingress.tgbot.dto.registeredUser.RegisteredUserUpdateRequest;
import az.ingress.tgbot.entity.RegisteredUser;
import org.mapstruct.BeanMapping;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;
import org.mapstruct.NullValuePropertyMappingStrategy;
import org.mapstruct.ReportingPolicy;

@Mapper(
        componentModel = "spring",
        unmappedTargetPolicy = ReportingPolicy.IGNORE
)
public interface RegisteredUserMapper {

    @Mapping(target = "id", source = "id")
    @Mapping(target = "username", source = "username")
    @Mapping(target = "role", source = "role")
    @Mapping(target = "active", source = "active")
    @Mapping(target = "createdAt", source = "createdAt")
    RegisteredUserResponse toResponse(RegisteredUser entity);


    @Mapping(target = "id", ignore = true)
    @Mapping(target = "passwordHash", ignore = true)
    @Mapping(target = "createdAt", ignore = true)
    @Mapping(target = "username", source = "username")
    @Mapping(target = "role", source = "role")
    @Mapping(target = "active", source = "active")
    RegisteredUser toEntity(RegisteredUserCreateRequest request);


    @BeanMapping(
            nullValuePropertyMappingStrategy =
                    NullValuePropertyMappingStrategy.IGNORE
    )
    @Mapping(target = "id", ignore = true)
    @Mapping(target = "passwordHash", ignore = true)
    @Mapping(target = "createdAt", ignore = true)
    @Mapping(target = "username", ignore = true)
    @Mapping(target = "role", source = "role")
    @Mapping(target = "active", ignore = true)
    void updateEntity(
            @MappingTarget RegisteredUser entity,
            RegisteredUserUpdateRequest request
    );
}