package az.ingress.tgbot.controller;

import az.ingress.tgbot.dto.registeredUser.RegisteredUserCreateRequest;
import az.ingress.tgbot.dto.registeredUser.RegisteredUserResponse;
import az.ingress.tgbot.dto.registeredUser.RegisteredUserUpdateRequest;
import az.ingress.tgbot.service.RegisteredUserService;
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
@RequestMapping("/api/admin/users")
@RequiredArgsConstructor
@Validated
@Tag(name = "RegisteredUser Management", description = "APIs for managing Registered User system accounts")
public class RegisteredUserController {

    private final RegisteredUserService registeredUserService;

    @PostMapping
    @Operation(summary = "Create Registered User", description = "Creates a new administrative account.")
    @ApiResponses({
            @ApiResponse(responseCode = "201", description = "Admin created successfully"),
            @ApiResponse(responseCode = "400", description = "Invalid payload or validation error"),
            @ApiResponse(responseCode = "409", description = "Username already exists")
    })
    public ResponseEntity<RegisteredUserResponse> create(@Valid @RequestBody RegisteredUserCreateRequest request) {
        return ResponseEntity
                .status(HttpStatus.CREATED)
                .body(registeredUserService.create(request));
    }

    @GetMapping
    @Operation(summary = "Get all admins", description = "Retrieves all registered admin accounts.")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Admin list retrieved successfully")
    })
    public ResponseEntity<List<RegisteredUserResponse>> getAll() {
        return ResponseEntity.ok(registeredUserService.getAll());
    }

    @GetMapping("/{id}")
    @Operation(summary = "Get admin by ID", description = "Retrieves admin details by ID.")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Admin user found"),
            @ApiResponse(responseCode = "404", description = "Admin user not found")
    })
    public ResponseEntity<RegisteredUserResponse> getById(
            @PathVariable @Positive(message = "ID must be positive") Long id
    ) {
        return ResponseEntity.ok(registeredUserService.getById(id));
    }

    @PutMapping("/{id}")
    @Operation(summary = "Update Registered User", description = "Updates Registered User details or credentials.")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Admin user updated successfully"),
            @ApiResponse(responseCode = "400", description = "Invalid payload or validation error"),
            @ApiResponse(responseCode = "404", description = "Admin user not found")
    })
    public ResponseEntity<RegisteredUserResponse> update(
            @PathVariable @Positive(message = "ID must be positive") Long id,
            @Valid @RequestBody RegisteredUserUpdateRequest request
    ) {
        return ResponseEntity.ok(registeredUserService.update(id, request));
    }

    @DeleteMapping("/{id}")
    @Operation(summary = "Delete admin user", description = "Deletes an admin user record.")
    @ApiResponses({
            @ApiResponse(responseCode = "204", description = "Admin deleted successfully"),
            @ApiResponse(responseCode = "404", description = "Admin user not found")
    })
    public ResponseEntity<Void> delete(
            @PathVariable @Positive(message = "ID must be positive") Long id
    ) {
        registeredUserService.delete(id);
        return ResponseEntity.noContent().build();
    }
}