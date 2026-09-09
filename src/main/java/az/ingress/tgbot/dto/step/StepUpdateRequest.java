package az.ingress.tgbot.dto;

import lombok.Data;

@Data
public class StepUpdateRequest {

    private String title;
    private Integer orderIndex;
    private Boolean isActive;
}