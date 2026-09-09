package az.ingress.tgbot.dto;

import jakarta.validation.constraints.NotBlank;
import lombok.Data;

@Data
public class SurveyCreateRequest {

    @NotBlank(message = "Survey title is required")
    private String title;

    private String description;
    private Boolean isActive = false;
}