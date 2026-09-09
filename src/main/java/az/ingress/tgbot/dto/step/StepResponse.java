package az.ingress.tgbot.dto;

import com.example.surveybot.dto.question.QuestionResponse;
import lombok.Builder;
import lombok.Data;

import java.util.List;

@Data
@Builder
public class StepResponse {

    private Long id;
    private Long surveyId;
    private String title;
    private Integer orderIndex;
    private Boolean isActive;
    private List<QuestionResponse> questions;
}