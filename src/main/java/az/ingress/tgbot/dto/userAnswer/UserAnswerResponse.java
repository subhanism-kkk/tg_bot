package az.ingress.tgbot.dto.userAnswer;

import lombok.Builder;
import lombok.Data;

import java.time.LocalDateTime;
import java.util.List;

@Data
@Builder
public class UserAnswerResponse {

    private Long id;
    private Long telegramUserId;
    private Long questionId;
    private String questionText;
    private String answerText;
    private List<Long> selectedOptionIds;
    private LocalDateTime answeredAt;
}