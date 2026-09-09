package az.ingress.tgbot.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

@Data
public class StepCreateRequest {

    @NotBlank(message = "Step title is required")
    private String title;

    @NotNull(message = "Order index is required")
    private Integer orderIndex;

    private Boolean isActive = true;
}