package az.ingress.tgbot.dto;

import lombok.Data;

@Data
public class SurveyUpdateRequest {

    private String title;
    private String description;
    private Boolean isActive;
}