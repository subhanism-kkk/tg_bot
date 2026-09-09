package az.ingress.tgbot.dto.adminUser;

import az.ingress.tgbot.enums.AdminRole;
import lombok.Builder;
import lombok.Data;

import java.time.LocalDateTime;

@Data
@Builder
public class AdminUserResponse {

    private Long id;
    private String username;
    private AdminRole role;
    private LocalDateTime createdAt;
}