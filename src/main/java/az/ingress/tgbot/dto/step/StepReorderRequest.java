package az.ingress.tgbot.dto.step;

import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

import java.util.List;

@Data
public class StepReorderRequest {

    @NotEmpty(message = "Step IDs cannot be empty.")
    private List<@NotNull Long> stepIds;
}