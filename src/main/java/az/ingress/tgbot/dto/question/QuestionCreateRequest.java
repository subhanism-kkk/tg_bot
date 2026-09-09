package az.ingress.tgbot.dto.question;

import az.ingress.tgbot.dto.questionOption.QuestionOptionCreateRequest;
import az.ingress.tgbot.enums.QuestionType;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

import java.util.List;

@Data
public class QuestionCreateRequest {

    @NotBlank(message = "Question text is required")
    private String text;

    @NotNull(message = "Question type is required")
    private QuestionType type;

    private Boolean isRequired = true;

    @NotNull(message = "Order index is required")
    private Long orderIndex;

    private String validationRegex;
    private String nextQuestionLogic;

    @Valid
    private List<QuestionOptionCreateRequest> options;
}