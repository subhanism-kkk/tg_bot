package az.ingress.tgbot.dto.questionOption;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

@Data
public class QuestionOptionCreateRequest {

    @NotBlank(message = "Option text is required")
    private String text;

    @NotBlank(message = "Option value is required")
    private String value;

    @NotNull(message = "Order index is required")
    private Long orderIndex;
}