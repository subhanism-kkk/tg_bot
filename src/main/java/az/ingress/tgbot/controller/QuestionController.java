package az.ingress.tgbot.controller;

import az.ingress.tgbot.dto.question.QuestionCreateRequest;
import az.ingress.tgbot.dto.question.QuestionResponse;
import az.ingress.tgbot.dto.question.QuestionUpdateRequest;
import az.ingress.tgbot.service.QuestionService;
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
@Tag(name = "Question Management", description = "APIs for managing step questions")
public class QuestionController {

    private final QuestionService questionService;

    @PostMapping("/steps/{stepId}/questions")
    @Operation(summary = "Create question for step", description = "Creates a new question under a step.")
    @ApiResponses({
            @ApiResponse(responseCode = "201", description = "Question created successfully"),
            @ApiResponse(responseCode = "400", description = "Invalid payload or validation error"),
            @ApiResponse(responseCode = "404", description = "Parent step not found")
    })
    public ResponseEntity<QuestionResponse> create(
            @PathVariable @Positive(message = "Step ID must be positive") Long stepId,
            @Valid @RequestBody QuestionCreateRequest request
    ) {
        return ResponseEntity
                .status(HttpStatus.CREATED)
                .body(questionService.create(stepId, request));
    }

    @GetMapping("/steps/{stepId}/questions")
    @Operation(summary = "Get questions by step ID", description = "Retrieves all questions for a specific step.")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Questions retrieved successfully"),
            @ApiResponse(responseCode = "404", description = "Parent step not found")
    })
    public ResponseEntity<List<QuestionResponse>> getByStepId(
            @PathVariable @Positive(message = "Step ID must be positive") Long stepId
    ) {
        return ResponseEntity.ok(questionService.getByStepId(stepId));
    }

    @GetMapping("/questions/{id}")
    @Operation(summary = "Get question by ID", description = "Retrieves question details by ID.")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Question found"),
            @ApiResponse(responseCode = "404", description = "Question not found")
    })
    public ResponseEntity<QuestionResponse> getById(
            @PathVariable @Positive(message = "ID must be positive") Long id
    ) {
        return ResponseEntity.ok(questionService.getById(id));
    }

    @PutMapping("/questions/{id}")
    @Operation(summary = "Update question", description = "Updates question configuration and text.")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Question updated successfully"),
            @ApiResponse(responseCode = "400", description = "Invalid payload or validation error"),
            @ApiResponse(responseCode = "404", description = "Question not found")
    })
    public ResponseEntity<QuestionResponse> update(
            @PathVariable @Positive(message = "ID must be positive") Long id,
            @Valid @RequestBody QuestionUpdateRequest request
    ) {
        return ResponseEntity.ok(questionService.update(id, request));
    }

    @DeleteMapping("/questions/{id}")
    @Operation(summary = "Delete question", description = "Deletes a question record.")
    @ApiResponses({
            @ApiResponse(responseCode = "204", description = "Question deleted successfully"),
            @ApiResponse(responseCode = "404", description = "Question not found")
    })
    public ResponseEntity<Void> delete(
            @PathVariable @Positive(message = "ID must be positive") Long id
    ) {
        questionService.delete(id);
        return ResponseEntity.noContent().build();
    }
}