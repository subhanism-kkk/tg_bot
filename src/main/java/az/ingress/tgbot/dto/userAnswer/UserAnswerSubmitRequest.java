package az.ingress.tgbot.dto;

import jakarta.validation.constraints.NotNull;
import lombok.Data;

import java.util.List;

@Data
public class UserAnswerSubmitRequest {

    @NotNull(message = "Telegram user ID is required")
    private Long telegramUserId;

    @NotNull(message = "Question ID is required")
    private Long questionId;

    private String answerText;
    private List<Long> selectedOptionIds;
}