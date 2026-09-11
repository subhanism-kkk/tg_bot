package az.ingress.tgbot.controller;

import az.ingress.tgbot.dto.adminUser.AdminUserResponse;
import az.ingress.tgbot.dto.step.StepCreateRequest;
import az.ingress.tgbot.dto.step.StepResponse;
import az.ingress.tgbot.dto.step.StepUpdateRequest;
import az.ingress.tgbot.service.StepService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import jakarta.validation.constraints.Positive;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/admin")
@RequiredArgsConstructor
@Validated
@Tag(name = "Step Management", description = "APIs for managing survey steps")
public class StepController {

    private final StepService stepService;

    @PostMapping("/surveys/{surveyId}/steps")
    @Operation(summary = "Create step for survey", description = "Creates a step assigned to a specific survey.")
    @ApiResponses({
            @ApiResponse(responseCode = "201", description = "Step created successfully"),
            @ApiResponse(responseCode = "400", description = "Invalid payload or validation error"),
            @ApiResponse(responseCode = "404", description = "Parent survey not found")
    })
    public ResponseEntity<StepResponse> create(
            @PathVariable @Positive(message = "Survey ID must be positive") Long surveyId,
            @Valid @RequestBody StepCreateRequest request
    ) {
        return ResponseEntity
                .status(HttpStatus.CREATED)
                .body(stepService.create(surveyId, request));
    }

    @GetMapping("/surveys/{surveyId}/steps")
    @Operation(summary = "Get steps by survey ID", description = "Retrieves all steps for a given survey ordered by order index.")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Steps retrieved successfully"),
            @ApiResponse(responseCode = "404", description = "Parent survey not found")
    })
    public ResponseEntity<List<StepResponse>> getBySurveyId(
            @PathVariable @Positive(message = "Survey ID must be positive") Long surveyId
    ) {
        return ResponseEntity.ok(stepService.getBySurveyId(surveyId));
    }

    @GetMapping("/steps/{id}")
    @Operation(summary = "Get step by ID", description = "Retrieves step details by ID.")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Step found"),
            @ApiResponse(responseCode = "404", description = "Step not found")
    })
    public ResponseEntity<StepResponse> getById(
            @PathVariable @Positive(message = "ID must be positive") Long id
    ) {
        return ResponseEntity.ok(stepService.getById(id));
    }

    @GetMapping
    @Operation(summary = "Get all steps", description = "Retrieves all of te steps.")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Step list retrieved successfully")
    })
    public ResponseEntity<List<StepResponse>> getAll() {
        return ResponseEntity.ok(stepService.getAll());
    }

    @PutMapping("/steps/{id}")
    @Operation(summary = "Update step", description = "Updates an existing step by ID.")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Step updated successfully"),
            @ApiResponse(responseCode = "400", description = "Invalid payload or validation error"),
            @ApiResponse(responseCode = "404", description = "Step not found")
    })
    public ResponseEntity<StepResponse> update(
            @PathVariable @Positive(message = "ID must be positive") Long id,
            @Valid @RequestBody StepUpdateRequest request
    ) {
        return ResponseEntity.ok(stepService.update(id, request));
    }

    @DeleteMapping("/steps/{id}")
    @Operation(summary = "Delete step", description = "Deletes a step record.")
    @ApiResponses({
            @ApiResponse(responseCode = "204", description = "Step deleted successfully"),
            @ApiResponse(responseCode = "404", description = "Step not found")
    })
    public ResponseEntity<Void> delete(
            @PathVariable @Positive(message = "ID must be positive") Long id
    ) {
        stepService.delete(id);
        return ResponseEntity.noContent().build();
    }
}