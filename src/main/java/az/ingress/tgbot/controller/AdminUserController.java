package az.ingress.tgbot.controller;

import az.ingress.tgbot.dto.adminUser.AdminUserCreateRequest;
import az.ingress.tgbot.dto.adminUser.AdminUserResponse;
import az.ingress.tgbot.dto.adminUser.AdminUserUpdateRequest;
import az.ingress.tgbot.service.AdminUserService;
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
@Tag(name = "Admin User Management", description = "APIs for managing admin system accounts")
public class AdminUserController {

    private final AdminUserService adminUserService;

    @PostMapping
    @Operation(summary = "Create admin user", description = "Creates a new administrative account.")
    @ApiResponses({
            @ApiResponse(responseCode = "201", description = "Admin created successfully"),
            @ApiResponse(responseCode = "400", description = "Invalid payload or validation error"),
            @ApiResponse(responseCode = "409", description = "Username already exists")
    })
    public ResponseEntity<AdminUserResponse> create(@Valid @RequestBody AdminUserCreateRequest request) {
        return ResponseEntity
                .status(HttpStatus.CREATED)
                .body(adminUserService.create(request));
    }

    @GetMapping
    @Operation(summary = "Get all admins", description = "Retrieves all registered admin accounts.")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Admin list retrieved successfully")
    })
    public ResponseEntity<List<AdminUserResponse>> getAll() {
        return ResponseEntity.ok(adminUserService.getAll());
    }

    @GetMapping("/{id}")
    @Operation(summary = "Get admin by ID", description = "Retrieves admin details by ID.")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Admin user found"),
            @ApiResponse(responseCode = "404", description = "Admin user not found")
    })
    public ResponseEntity<AdminUserResponse> getById(
            @PathVariable @Positive(message = "ID must be positive") Long id
    ) {
        return ResponseEntity.ok(adminUserService.getById(id));
    }

    @PutMapping("/{id}")
    @Operation(summary = "Update admin", description = "Updates administrative details or credentials.")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Admin user updated successfully"),
            @ApiResponse(responseCode = "400", description = "Invalid payload or validation error"),
            @ApiResponse(responseCode = "404", description = "Admin user not found")
    })
    public ResponseEntity<AdminUserResponse> update(
            @PathVariable @Positive(message = "ID must be positive") Long id,
            @Valid @RequestBody AdminUserUpdateRequest request
    ) {
        return ResponseEntity.ok(adminUserService.update(id, request));
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
        adminUserService.delete(id);
        return ResponseEntity.noContent().build();
    }
}