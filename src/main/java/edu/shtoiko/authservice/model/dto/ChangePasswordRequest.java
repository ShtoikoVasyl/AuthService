package edu.shtoiko.authservice.model.dto;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
public class ChangePasswordRequest {
    private String userId;
    private String password;
    private String newPassword;
}
