package az.ingress.tgbot.dto.registeredUser;

import az.ingress.tgbot.enums.UserRole;
import lombok.Builder;
import lombok.Data;

import java.time.LocalDateTime;

@Data
@Builder
public class RegisteredUserResponse {

    private Long id;
    private String username;
    private UserRole role;
    private boolean active;
    private LocalDateTime createdAt;
}