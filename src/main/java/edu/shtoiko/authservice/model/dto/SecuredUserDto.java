package edu.shtoiko.authservice.model.dto;

import edu.shtoiko.authservice.model.Role;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.List;
@NoArgsConstructor
@Setter
@Getter
public class SecuredUserDto {

    private Long id;

    private String email;

    private List<Role> roles;
}
