package az.ingress.tgbot.dto.auth;

import az.ingress.tgbot.enums.AdminRole;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class RegisterRequest {

    @NotBlank(message = "Username cannot be blank.")
    @Size(min = 3, max = 50, message = "Username must be between 3 and 50 characters.")
    private String username;

    @NotBlank(message = "Password cannot be blank.")
    @Size(min = 6, max = 100, message = "Password must be at least 6 characters.")
    private String password;

    private AdminRole role;
}