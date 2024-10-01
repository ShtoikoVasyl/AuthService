package edu.shtoiko.authservice.model.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
public class RoleRequest {
    @NotBlank(message = "Role name cannot be blank")
    @Size(max = 30, message = "Role name cannot exceed 30 characters")
    @Pattern(regexp = "^[A-Z_]+$", message = "Role name must contain only uppercase letters and underscores, no spaces, numbers, or special characters allowed")
    private String name;
}
