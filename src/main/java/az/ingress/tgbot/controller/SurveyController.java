package az.ingress.tgbot.controller;


import az.ingress.tgbot.dto.survey.SurveyCreateRequest;
import az.ingress.tgbot.dto.survey.SurveyResponse;
import az.ingress.tgbot.dto.survey.SurveyUpdateRequest;
import az.ingress.tgbot.service.SurveyService;
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
@RequestMapping("/api/admin/surveys")
@RequiredArgsConstructor
@Validated
@Tag(name = "Survey Management", description = "APIs for managing surveys")
public class SurveyController {

    private final SurveyService surveyService;

    @PostMapping
    @Operation(summary = "Create survey", description = "Creates a new survey record.")
    @ApiResponses({
            @ApiResponse(responseCode = "201", description = "Survey created successfully"),
            @ApiResponse(responseCode = "400", description = "Invalid payload or validation error"),
            @ApiResponse(responseCode = "409", description = "Survey title already exists")
    })
    public ResponseEntity<SurveyResponse> create(@Valid @RequestBody SurveyCreateRequest request){

        return ResponseEntity
                .status(HttpStatus.CREATED)
                .body(surveyService.create(request));

    }
    @GetMapping
    @Operation(summary = "Get all surveys", description = "Retrieves all surveys.")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Surveys retrieved successfully")
    })
    public ResponseEntity<List<SurveyResponse>> getAll() {
        return ResponseEntity.ok(surveyService.getAll());
    }

    @GetMapping("/{id}")
    @Operation(summary = "Get survey by ID", description = "Retrieves survey details by ID.")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Survey found"),
            @ApiResponse(responseCode = "404", description = "Survey not found")
    })
    public ResponseEntity<SurveyResponse> getById(
            @PathVariable @Positive(message = "ID must be a positive number") Long id
    ) {
        return ResponseEntity.ok(surveyService.getById(id));
    }

    @PutMapping("/{id}")
    @Operation(summary = "Update survey", description = "Updates an existing survey by ID.")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Survey updated successfully"),
            @ApiResponse(responseCode = "400", description = "Invalid payload or validation error"),
            @ApiResponse(responseCode = "404", description = "Survey not found")
    })
    public ResponseEntity<SurveyResponse> update(
            @PathVariable @Positive(message = "ID must be a positive number") Long id,
            @Valid @RequestBody SurveyUpdateRequest request
    ) {
        return ResponseEntity.ok(surveyService.update(id, request));
    }

    @PatchMapping("/{id}/activate")
    @Operation(summary = "Activate survey", description = "Sets survey status to active.")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Survey activated successfully"),
            @ApiResponse(responseCode = "400", description = "Survey is already active"),
            @ApiResponse(responseCode = "404", description = "Survey not found")
    })
    public ResponseEntity<Void> activate(
            @PathVariable @Positive(message = "ID must be a positive number") Long id
    ) {
        surveyService.activate(id);
        return ResponseEntity.ok().build();
    }

    @PatchMapping("/{id}/deactivate")
    @Operation(summary = "Deactivate survey", description = "Sets survey status to inactive.")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Survey deactivated successfully"),
            @ApiResponse(responseCode = "400", description = "Survey is already inactive"),
            @ApiResponse(responseCode = "404", description = "Survey not found")
    })
    public ResponseEntity<Void> deactivate(
            @PathVariable @Positive(message = "ID must be a positive number") Long id
    ) {
        surveyService.deactivate(id);
        return ResponseEntity.ok().build();
    }

    @DeleteMapping("/{id}")
    @Operation(summary = "Delete survey", description = "Permanently deletes a survey record.")
    @ApiResponses({
            @ApiResponse(responseCode = "204", description = "Survey deleted successfully"),
            @ApiResponse(responseCode = "404", description = "Survey not found")
    })
    public ResponseEntity<Void> delete(
            @PathVariable @Positive(message = "ID must be a positive number") Long id
    ) {
        surveyService.delete(id);
        return ResponseEntity.noContent().build();
    }
}
