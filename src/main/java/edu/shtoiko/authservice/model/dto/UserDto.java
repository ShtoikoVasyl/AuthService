package edu.shtoiko.authservice.model.dto;

import jakarta.persistence.Column;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class UserDto {
    private Long id;

    private String email;

    private String password;
}
