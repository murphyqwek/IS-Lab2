package com.example.is.controller;

import com.example.is.dto.request.LoginRequest;
import com.example.is.dto.request.RegisterRequest;
import com.example.is.dto.response.AuthResponse;
import com.example.is.entity.User;
import com.example.is.entity.UserRole;
import com.example.is.service.UserService;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpSession;
import org.springframework.http.HttpStatus;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/auth")
public class AuthController {
    private final UserService userService;
    private final AuthenticationManager authenticationManager;

    public AuthController(UserService userService, AuthenticationManager authenticationManager) {
        this.userService = userService;
        this.authenticationManager = authenticationManager;
    }

    @PostMapping("/register")
    @ResponseStatus(HttpStatus.CREATED)
    public AuthResponse register(@RequestBody RegisterRequest request) {

        User user = userService.registerNewUser(request.username(), request.password());

        return new AuthResponse(
                user.getId(),
                user.getUsername(),
                user.getRole()
        );
    }


    @PostMapping("/login")
    public AuthResponse login(@RequestBody LoginRequest request) {

        Authentication authentication =
                authenticationManager.authenticate(
                        new UsernamePasswordAuthenticationToken(
                                request.username(),
                                request.password()
                        )
                );


        SecurityContextHolder.getContext().setAuthentication(authentication);


        UserDetails userDetails = (UserDetails) authentication.getPrincipal();


        return new AuthResponse(
                null,
                userDetails.getUsername(),
                userDetails.getAuthorities()
                        .stream()
                        .findFirst()
                        .map(authority ->
                                UserRole.valueOf(
                                        authority.getAuthority().replace("ROLE_", "")
                                )
                        )
                        .orElse(null)
        );
    }


    @PostMapping("/logout")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void logout(HttpServletRequest request) {

        SecurityContextHolder.clearContext();

        HttpSession session = request.getSession(false);

        if(session != null) {
            session.invalidate();
        }
    }
}