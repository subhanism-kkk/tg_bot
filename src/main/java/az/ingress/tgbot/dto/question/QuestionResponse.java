package az.ingress.tgbot.dto;

import az.ingress.tgbot.enums.QuestionType;
import lombok.Builder;
import lombok.Data;

import java.util.List;

@Data
@Builder
public class QuestionResponse {

    private Long id;
    private Long stepId;
    private String text;
    private QuestionType type;
    private Boolean isRequired;
    private Integer orderIndex;
    private String validationRegex;
    private String nextQuestionLogic;
    private List<QuestionOptionResponse> options;
}