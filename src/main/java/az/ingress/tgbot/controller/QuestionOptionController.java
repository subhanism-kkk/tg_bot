package az.ingress.tgbot.controller;

import az.ingress.tgbot.dto.questionOption.QuestionOptionCreateRequest;
import az.ingress.tgbot.dto.questionOption.QuestionOptionResponse;
import az.ingress.tgbot.dto.questionOption.QuestionOptionUpdateRequest;
import az.ingress.tgbot.service.QuestionOptionService;
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
@Tag(name = "Question Option Management", description = "APIs for managing radio and checkbox option choices")
public class QuestionOptionController {

    private final QuestionOptionService questionOptionService;

    @PostMapping("/questions/{questionId}/options")
    @Operation(summary = "Create option for question", description = "Adds a new selectable option to a multiple-choice question.")
    @ApiResponses({
            @ApiResponse(responseCode = "201", description = "Option created successfully"),
            @ApiResponse(responseCode = "400", description = "Invalid payload or validation error"),
            @ApiResponse(responseCode = "404", description = "Parent question not found")
    })
    public ResponseEntity<QuestionOptionResponse> create(
            @PathVariable @Positive(message = "Question ID must be positive") Long questionId,
            @Valid @RequestBody QuestionOptionCreateRequest request
    ) {
        return ResponseEntity
                .status(HttpStatus.CREATED)
                .body(questionOptionService.create(questionId, request));
    }

    @GetMapping("/questions/{questionId}/options")
    @Operation(summary = "Get options by question ID", description = "Retrieves all selectable options for a question.")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Options retrieved successfully"),
            @ApiResponse(responseCode = "404", description = "Parent question not found")
    })
    public ResponseEntity<List<QuestionOptionResponse>> getByQuestionId(
            @PathVariable @Positive(message = "Question ID must be positive") Long questionId
    ) {
        return ResponseEntity.ok(questionOptionService.getByQuestionId(questionId));
    }

    @GetMapping("/options/{id}")
    @Operation(summary = "Get option by ID", description = "Retrieves option details by ID.")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Option found"),
            @ApiResponse(responseCode = "404", description = "Option not found")
    })
    public ResponseEntity<QuestionOptionResponse> getById(
            @PathVariable @Positive(message = "ID must be positive") Long id
    ) {
        return ResponseEntity.ok(questionOptionService.getById(id));
    }

    @PutMapping("/options/{id}")
    @Operation(summary = "Update option", description = "Updates an option's display text or value.")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Option updated successfully"),
            @ApiResponse(responseCode = "400", description = "Invalid payload or validation error"),
            @ApiResponse(responseCode = "404", description = "Option not found")
    })
    public ResponseEntity<QuestionOptionResponse> update(
            @PathVariable @Positive(message = "ID must be positive") Long id,
            @Valid @RequestBody QuestionOptionUpdateRequest request
    ) {
        return ResponseEntity.ok(questionOptionService.update(id, request));
    }

    @DeleteMapping("/options/{id}")
    @Operation(summary = "Delete option", description = "Deletes a question option.")
    @ApiResponses({
            @ApiResponse(responseCode = "204", description = "Option deleted successfully"),
            @ApiResponse(responseCode = "404", description = "Option not found")
    })
    public ResponseEntity<Void> delete(
            @PathVariable @Positive(message = "ID must be positive") Long id
    ) {
        questionOptionService.delete(id);
        return ResponseEntity.noContent().build();
    }
}