package edu.shtoiko.authservice.controller;

import edu.shtoiko.authservice.model.Role;
import edu.shtoiko.authservice.model.dto.RoleRequest;
import edu.shtoiko.authservice.service.RoleService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/role/")
@RequiredArgsConstructor
@Tag(name = "Role Controller", description = "Controller for managing roles")
public class RoleController {
    private final RoleService roleService;

    @Operation(summary = "Get all roles", description = "Retrieves a list of all roles in the system. Accessible to the users with ADMIN authority.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Roles retrieved successfully"),
            @ApiResponse(responseCode = "403", description = "Forbidden - Insufficient permissions"),
            @ApiResponse(responseCode = "500", description = "Internal server error")
    })
    @SecurityRequirement(name = "bearerAuth")
    @GetMapping
    @PreAuthorize("hasAuthority('ADMIN')")
    public ResponseEntity<?> getAllRoles(){
        return ResponseEntity.ok(roleService.getAllRoles());
    }

    @Operation(summary = "Create a new role", description = "Creates a new role in the system. Accessible to the users with ADMIN authority.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "201", description = "Role created successfully"),
            @ApiResponse(responseCode = "403", description = "Forbidden - Insufficient permissions"),
            @ApiResponse(responseCode = "500", description = "Internal server error")
    })
    @SecurityRequirement(name = "bearerAuth")
    @PostMapping
    @PreAuthorize("hasAuthority('ADMIN')")
    public ResponseEntity<?> createRole(
            @Parameter(description = "Name of the role to create", required = true)
            @Valid @RequestBody RoleRequest role){
        return new ResponseEntity<>(roleService.create(role), HttpStatus.CREATED);
    }
}