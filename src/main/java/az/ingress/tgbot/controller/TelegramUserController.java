package az.ingress.tgbot.controller;

import az.ingress.tgbot.dto.telegramUser.TelegramUserResponse;
import az.ingress.tgbot.service.TelegramUserService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/admin/telegram-users")
@RequiredArgsConstructor
@Validated
@Tag(name = "Telegram User Management", description = "APIs for viewing Telegram bot user profiles")
public class TelegramUserController {

    private final TelegramUserService telegramUserService;

    @GetMapping
    @Operation(summary = "Get all Telegram users", description = "Retrieves all registered Telegram bot users.")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Users retrieved successfully")
    })
    public ResponseEntity<List<TelegramUserResponse>> getAll() {
        return ResponseEntity.ok(telegramUserService.getAll());
    }

    @GetMapping("/{id}")
    @Operation(summary = "Get Telegram user by ID", description = "Retrieves Telegram user profile by database ID.")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "User found"),
            @ApiResponse(responseCode = "404", description = "User not found")
    })
    public ResponseEntity<TelegramUserResponse> getById(
            @PathVariable @Positive(message = "ID must be positive") Long id
    ) {
        return ResponseEntity.ok(telegramUserService.getById(id));
    }

    @GetMapping("/chat/{chatId}")
    @Operation(summary = "Get Telegram user by Chat ID", description = "Retrieves Telegram user profile by unique Telegram Chat ID.")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "User found"),
            @ApiResponse(responseCode = "404", description = "User not found")
    })
    public ResponseEntity<TelegramUserResponse> getByChatId(
            @PathVariable @NotNull(message = "Chat ID is required") Long chatId
    ) {
        return ResponseEntity.ok(telegramUserService.getByChatId(chatId));
    }
}