package az.ingress.tgbot.dto.questionOption;

import jakarta.validation.constraints.NotBlank;
import lombok.Data;

@Data
public class QuestionOptionCreateRequest {

    @NotBlank(message = "Option text is required")
    private String text;

    @NotBlank(message = "Option value is required")
    private String value;

    private Long orderIndex;
}