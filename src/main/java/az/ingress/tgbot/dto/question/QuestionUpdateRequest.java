package az.ingress.tgbot.dto;

import az.ingress.tgbot.enums.QuestionType;
import jakarta.validation.Valid;
import lombok.Data;

import java.util.List;

@Data
public class QuestionUpdateRequest {

    private String text;
    private QuestionType type;
    private Boolean isRequired;
    private Integer orderIndex;
    private String validationRegex;
    private String nextQuestionLogic;

    @Valid
    private List<QuestionOptionCreateRequest> options;
}