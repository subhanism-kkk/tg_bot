package az.ingress.tgbot.dto.step;

import lombok.Data;

@Data
public class StepUpdateRequest {

    private String title;
    private Long orderIndex;
    private Boolean isActive;
}