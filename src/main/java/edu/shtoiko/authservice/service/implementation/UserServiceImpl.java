package edu.shtoiko.authservice.service.implementation;

import edu.shtoiko.authservice.exception.ResponseException;
import edu.shtoiko.authservice.model.Role;
import edu.shtoiko.authservice.model.UserSession;
import edu.shtoiko.authservice.model.dto.ChangePasswordRequest;
import edu.shtoiko.authservice.model.dto.JwtResponse;
import edu.shtoiko.authservice.model.SecuredUser;
import edu.shtoiko.authservice.model.dto.SecuredUserDto;
import edu.shtoiko.authservice.model.dto.UserDto;
import edu.shtoiko.authservice.repository.UserRepository;
import edu.shtoiko.authservice.service.RoleService;
import edu.shtoiko.authservice.service.UserService;
import edu.shtoiko.authservice.service.UserSessionService;
import edu.shtoiko.authservice.utils.JwtTokenUtils;
import jakarta.persistence.EntityNotFoundException;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.modelmapper.ModelMapper;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import org.springframework.security.access.AccessDeniedException;
import org.springframework.web.server.ResponseStatusException;

import javax.naming.AuthenticationException;
import java.time.LocalDateTime;
import java.util.List;

@RequiredArgsConstructor
@Service
@Slf4j
public class UserServiceImpl implements UserService, UserDetailsService {

    private final PasswordEncoder passwordEncoder = new BCryptPasswordEncoder();

    private final JwtTokenUtils tokenUtils;

    private final UserRepository userRepository;

    private final UserSessionService userSessionService;

    private final ModelMapper modelMapper;

    private final RoleService roleService;

    //todo rewrite. UsersSession
    @Override
    public JwtResponse loginUser(String email, String password){
        SecuredUser user = getSecuredUserByEmail(email);
        if(passwordEncoder.matches(password, user.getPassword())){
            JwtResponse response = tokenUtils.createNewTokenPair(user);
            userSessionService.saveSession(user, response.refreshToken());
            return response;
        } else {
            log.warn("user's email : {} passwords mismatches", email);
            throw new ResponseException(HttpStatus.FORBIDDEN, "Invalid credentials");
        }
    }

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        return getSecuredUserByEmail(username);
    }

    //todo: session control
    @Override
    public boolean registerUser(UserDto userDto) {
        if(userRepository.existsByEmail(userDto.getEmail())){
            log.error("User with email {} already exist", userDto.getEmail());
            throw new ResponseException(HttpStatus.CONFLICT, "User with email " + userDto.getEmail() + " already exist");
        }
        SecuredUser newUser = modelMapper.map(userDto, SecuredUser.class);
        newUser.setRoles(List.of(new Role(1L, "ROLE_USER")));
        newUser.setSessions(List.of());
        userRepository.save(newUser);
        return userRepository.existsById(userDto.getId());
    }

    @Override
    @Transactional
    public String logout(String refreshToken) {
        userSessionService.deleteByRefreshToken(refreshToken);
        return "Logout successful";
    }

    //todo should compare session info, exception handling
    @Override
    public JwtResponse refreshToken(String refreshToken) {
        JwtResponse newTokenPair = tokenUtils.refreshToken(refreshToken);
        if (userSessionService.updateRefreshToken(refreshToken, newTokenPair.refreshToken())) {
            return newTokenPair;
        } else {
            throw new RuntimeException();
        }
    }

        // todo refactor
    @Override
    @Transactional
    public String changePassword(ChangePasswordRequest passwordRequest) {
        SecuredUser user = userRepository.findById(Long.parseLong(passwordRequest.getUserId())).orElseThrow(EntityNotFoundException::new);
        if(passwordEncoder.matches(passwordRequest.getPassword(), user.getPassword())){
            user.setPassword(passwordEncoder.encode(passwordRequest.getNewPassword()));
            userRepository.save(user);
            return "password changed successfully";
        } else {
            throw new ResponseException(HttpStatus.FORBIDDEN, "Invalid credentials");
        }
    }

    @Override
    public SecuredUserDto changeRoles(Long userId, List<String> roleNames) {
        List<Role> roles = roleService.getRolesByNames(roleNames);
        if(roles.isEmpty()){
            throw new ResponseException(HttpStatus.BAD_REQUEST, "Roles not found");
        }
        SecuredUser user = userRepository.findById(userId).orElseThrow(() -> new ResponseException(HttpStatus.BAD_REQUEST, "User not found"));
        user.setRoles(roles);
        return modelMapper.map(userRepository.save(user), SecuredUserDto.class);
    }

    @Override
    public SecuredUserDto getSecuredUserDtoById(Long userId) {
        return modelMapper.map(getSecuredUserById(userId), SecuredUserDto.class);
    }

    @Override
    public SecuredUserDto getSecuredUserDtoByEmail(String email) {
        SecuredUser user = getSecuredUserByEmail(email);
        if(user == null){
            throw new ResponseException(HttpStatus.BAD_REQUEST, "User not found");
        }
        return modelMapper.map(user, SecuredUserDto.class);
    }

    private SecuredUser getSecuredUserById(Long id){
        return userRepository.findById(id).orElseThrow(() -> new ResponseException(HttpStatus.BAD_REQUEST, "User not found"));
    }

    private SecuredUser getSecuredUserByEmail(String email){
        return userRepository.findByEmail(email);
    }
}
