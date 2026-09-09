package az.ingress.tgbot.dto.step;

import az.ingress.tgbot.dto.question.QuestionResponse;
import lombok.Builder;
import lombok.Data;

import java.util.List;

@Data
@Builder
public class StepResponse {

    private Long id;
    private Long surveyId;
    private String title;
    private Long orderIndex;
    private Boolean isActive;
    private List<QuestionResponse> questions;
}