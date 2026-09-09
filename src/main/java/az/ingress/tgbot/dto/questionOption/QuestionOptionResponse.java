package az.ingress.tgbot.dto.questionOption;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class QuestionOptionResponse {

    private Long id;
    private Long questionId;
    private String text;
    private String value;
    private Long orderIndex;
}