package az.ingress.tgbot.controller;

import az.ingress.tgbot.dto.userAnswer.UserAnswerResponse;
import az.ingress.tgbot.dto.userAnswer.UserAnswerSubmitRequest;
import az.ingress.tgbot.service.ExportService;
import az.ingress.tgbot.service.UserAnswerService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.util.List;

@RestController
@RequestMapping("/api/admin/results")
@RequiredArgsConstructor
@Validated
@Tag(name = "Results & Answers", description = "APIs for reviewing submission data and downloading reports")
public class ResultController {

    private final UserAnswerService userAnswerService;
    private final ExportService exportService;

    @GetMapping
    @Operation(summary = "Get filtered survey results", description = "Retrieves user answers filtered by survey ID, status, and date range.")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Results retrieved successfully"),
            @ApiResponse(responseCode = "400", description = "Invalid filter parameters")
    })
    public ResponseEntity<List<UserAnswerResponse>> getResults(
            @RequestParam(required = false) Long surveyId,
            @RequestParam(required = false) String status,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate from,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate to
    ) {
        return ResponseEntity.ok(userAnswerService.getResults(surveyId, status, from, to));
    }

    @GetMapping("/export")
    @Operation(summary = "Export results to Excel", description = "Generates and streams an Excel file (.xlsx) containing survey response records.")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Excel file generated successfully"),
            @ApiResponse(responseCode = "500", description = "Excel generation failed")
    })
    public ResponseEntity<byte[]> exportResults(
            @RequestParam(required = false) Long surveyId,
            @RequestParam(required = false) String status,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate from,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate to
    ) {
        byte[] excelBytes = exportService.exportResultsToExcel(surveyId, status, from, to);

        return ResponseEntity.ok()
                .header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=survey_results.xlsx")
                .contentType(MediaType.APPLICATION_OCTET_STREAM)
                .body(excelBytes);
    }

    @PostMapping("/submit")
    @Operation(summary = "Submit user answer", description = "Stores an individual answer submitted from the Telegram bot.")
    @ApiResponses({
            @ApiResponse(responseCode = "201", description = "Answer recorded successfully"),
            @ApiResponse(responseCode = "400", description = "Invalid payload"),
            @ApiResponse(responseCode = "404", description = "Telegram user or question not found")
    })
    public ResponseEntity<UserAnswerResponse> submitAnswer(@Valid @RequestBody UserAnswerSubmitRequest request) {
        return ResponseEntity
                .status(HttpStatus.CREATED)
                .body(userAnswerService.submitAnswer(request));
    }
}