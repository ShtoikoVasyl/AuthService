package edu.shtoiko.authservice.controller;

import edu.shtoiko.authservice.model.dto.*;
import edu.shtoiko.authservice.service.UserService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RequiredArgsConstructor
@RestController
@RequestMapping("/auth/")
@Tag(name = "Auth Controller",
    description = "Controller for handling authentication, user management, and security operations")
public class AuthController {

    private final UserService userService;

    @Operation(summary = "Login user",
        description = "Authenticates a user and returns a JWT token upon successful login.")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Login successful"),
        @ApiResponse(responseCode = "403", description = "Forbidden - Invalid credentials"),
        @ApiResponse(responseCode = "500", description = "Internal server error")
    })
    @PostMapping("/login")
    public ResponseEntity<?> login(
        @Parameter(description = "Login request with email and password",
            required = true) @RequestBody LoginRequest loginRequest) {
        return ResponseEntity.ok(userService.loginUser(loginRequest.getEmail(), loginRequest.getPassword()));
    }

    @Operation(summary = "Register a new user",
        description = "Registers a new user in the system. Accessible only to users with USERMANAGER_WRITE authority.")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "201", description = "User registered successfully"),
        @ApiResponse(responseCode = "403", description = "Forbidden - Insufficient permissions"),
        @ApiResponse(responseCode = "500", description = "Internal server error")
    })
    @SecurityRequirement(name = "bearerAuth")
    @PreAuthorize("hasAuthority('USERMANAGER_WRITE')")
    @PostMapping("/user/register")
    public ResponseEntity<?> registerNewUser(
        @Parameter(description = "User details for registration", required = true) @RequestBody UserDto userDto) {
        return new ResponseEntity<>(userService.registerUser(userDto), HttpStatus.CREATED);
    }

    @Operation(summary = "Logout user", description = "Logs out a user and invalidates the provided refresh token.")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Logout successful"),
        @ApiResponse(responseCode = "400", description = "Bad Request - Token not found or invalid"),
        @ApiResponse(responseCode = "500", description = "Internal server error")
    })
    @PostMapping("/logout")
    public ResponseEntity<?> logout(
        @Parameter(description = "Refresh token to be invalidated", required = true) @RequestBody String refreshToken) {
        return ResponseEntity.ok(userService.logout(refreshToken));
    }

    @Operation(summary = "Refresh JWT token", description = "Refreshes the JWT token using the provided refresh token.")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Token refreshed successfully"),
        @ApiResponse(responseCode = "400", description = "Bad Request - Invalid token"),
        @ApiResponse(responseCode = "500", description = "Internal server error")
    })
    @PostMapping("/refresh-token")
    public ResponseEntity<?> refreshToken(
        @Parameter(description = "Refresh token for getting a new access token",
            required = true) @RequestBody String refreshToken) {
        try {
            return ResponseEntity.ok(userService.refreshToken(refreshToken));
        } catch (IllegalArgumentException ex) {
            return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
        }
    }

    @Operation(summary = "Change user password",
        description = "Changes the password for a user. Accessible to the user themselves or users with USERMANAGER_WRITE authority.")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Password changed successfully"),
        @ApiResponse(responseCode = "403", description = "Forbidden - Insufficient permissions or invalid request"),
        @ApiResponse(responseCode = "500", description = "Internal server error")
    })
    @SecurityRequirement(name = "bearerAuth")
    @PreAuthorize("#passwordRequest.userId == authentication.details or hasAuthority('USERMANAGER_WRITE')")
    @PostMapping("/user/change-password")
    public ResponseEntity<?> changePassword(
        @Parameter(description = "Password change request containing user ID and new password",
            required = true) @RequestBody ChangePasswordRequest passwordRequest) {
        try {
            return ResponseEntity.ok(userService.changePassword(passwordRequest));
        } catch (IllegalArgumentException ex) {
            return new ResponseEntity<>(HttpStatus.FORBIDDEN);
        }
    }

    @Operation(summary = "Change user roles",
        description = "Changes the roles assigned to a user. Accessible only to users with ADMIN authority.")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "User roles changed successfully"),
        @ApiResponse(responseCode = "403", description = "Forbidden - Insufficient permissions"),
        @ApiResponse(responseCode = "500", description = "Internal server error")
    })
    @SecurityRequirement(name = "bearerAuth")
    @PreAuthorize("hasAuthority('ADMIN')")
    @PutMapping("/user/roles/{userId}")
    public ResponseEntity<?> changeUserRoles(
        @Parameter(description = "ID of the user whose roles will be changed",
            required = true) @PathVariable Long userId,
        @Parameter(description = "List of new roles to assign to the user",
            required = true) @RequestBody List<String> roles) {
        return ResponseEntity.ok(userService.changeRoles(userId, roles));
    }

    @Operation(summary = "Get secured user by ID",
        description = "Retrieves secured user details including roles and active sessions. Accessible to the user themselves or users with ADMIN authority.")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "User details retrieved successfully"),
        @ApiResponse(responseCode = "403", description = "Forbidden - Insufficient permissions"),
        @ApiResponse(responseCode = "500", description = "Internal server error")
    })
    @SecurityRequirement(name = "bearerAuth")
    @PreAuthorize("#userId == authentication.details or hasAuthority('ADMIN')")
    @GetMapping("/user/{userId}")
    public ResponseEntity<?> getSecuredUserById(
        @Parameter(description = "ID of the user to retrieve", required = true) @PathVariable String userId) {
        SecuredUserDto userDto = userService.getSecuredUserDtoById(Long.parseLong(userId));
        return ResponseEntity.ok(userDto);
    }

    @Operation(summary = "Get secured user by email",
        description = "Retrieves secured user details by email, including roles and active sessions. Accessible only to users with ADMIN authority.")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "User details retrieved successfully"),
        @ApiResponse(responseCode = "403", description = "Forbidden - Insufficient permissions"),
        @ApiResponse(responseCode = "500", description = "Internal server error")
    })
    @SecurityRequirement(name = "bearerAuth")
    @PreAuthorize("hasAuthority('ADMIN')")
    @GetMapping("/user")
    public ResponseEntity<?> getSecuredUserByEmail(
        @Parameter(description = "Email address of the user to retrieve", required = true) @RequestParam String email) {
        return ResponseEntity.ok(userService.getSecuredUserDtoByEmail(email));
    }
}