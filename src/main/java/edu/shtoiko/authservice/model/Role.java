package edu.shtoiko.authservice.model;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
import lombok.*;
import org.springframework.security.core.GrantedAuthority;

@Getter
@Setter
@NoArgsConstructor
@ToString
@Entity
@Table(name = "roles")
@AllArgsConstructor
public class Role implements GrantedAuthority {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private long id;

    @Column(name = "name", nullable = false, unique = true)
    @NotBlank(message = "Role name cannot be blank")
    @Size(max = 30, message = "Role name cannot exceed 30 characters")
    @Pattern(regexp = "^[A-Z_]+$",
        message = "Role name must contain only uppercase letters and underscores, no spaces, numbers, or special characters allowed")
    private String name;

    @Override
    public String getAuthority() {
        return name;
    }

    public Role(String name) {
        this.name = name;
    }
}