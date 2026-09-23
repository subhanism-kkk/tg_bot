package az.ingress.tgbot.dto.questionOption;

import jakarta.validation.constraints.NotBlank;
import lombok.Data;

@Data
public class QuestionOptionUpdateRequest {

    @NotBlank(message = "Option text is required")
    private String text;

    private String value;

    private Long orderIndex;
}