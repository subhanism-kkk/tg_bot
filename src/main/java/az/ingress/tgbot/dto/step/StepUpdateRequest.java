package az.ingress.tgbot.dto.step;

import jakarta.validation.constraints.NotBlank;
import lombok.Data;

@Data
public class StepUpdateRequest {

    @NotBlank(message = "Step title is required")
    private String title;

    private Long orderIndex;

    private Boolean isActive = true;
}