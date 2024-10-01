package edu.shtoiko.authservice.service;

import edu.shtoiko.authservice.model.dto.*;

import org.springframework.security.access.AccessDeniedException;

import java.util.List;

public interface UserService {
    JwtResponse loginUser(String email, String password);

    boolean registerUser(UserDto userDto);

    String logout(String refreshToken);

    JwtResponse refreshToken(String refreshToken);

    String changePassword(ChangePasswordRequest passwordRequest);

    SecuredUserDto changeRoles(Long userId, List<String> roleNames);

    SecuredUserDto getSecuredUserDtoById(Long userId);

    SecuredUserDto getSecuredUserDtoByEmail(String email);
}
