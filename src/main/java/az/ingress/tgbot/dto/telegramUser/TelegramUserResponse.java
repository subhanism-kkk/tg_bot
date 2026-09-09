package az.ingress.tgbot.dto;

import az.ingress.tgbot.enums.RegistrationStatus;
import lombok.Builder;
import lombok.Data;

import java.time.LocalDateTime;

@Data
@Builder
public class TelegramUserResponse {

    private Long id;
    private Long chatId;
    private String username;
    private String firstName;
    private String lastName;
    private String phoneNumber;
    private RegistrationStatus registrationStatus;
    private Long currentStepId;
    private Long currentQuestionId;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
}