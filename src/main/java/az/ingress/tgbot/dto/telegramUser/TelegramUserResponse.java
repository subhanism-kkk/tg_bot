package az.ingress.tgbot.dto.telegramUser;

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
    private String draftCheckboxSelections;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
}