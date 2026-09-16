package az.ingress.tgbot.dto.question;

import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

import java.util.List;

@Data
public class QuestionReorderRequest {

    @NotEmpty(message = "Question IDs cannot be empty.")
    private List<@NotNull Long> questionIds;
}