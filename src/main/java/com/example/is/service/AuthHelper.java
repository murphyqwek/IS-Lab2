package com.example.is.service;

import com.example.is.entity.UserRole;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Service;

@Service
public class AuthHelper {
    public UserRole getUserRole(Authentication authentication) {
        UserDetails userDetails = (UserDetails) authentication.getPrincipal();

        return getUserRole(userDetails);
    }

    public UserRole getUserRole(UserDetails userDetails) {
        UserRole role = userDetails.getAuthorities()
                .stream()
                .findFirst()
                .map(GrantedAuthority::getAuthority)
                .map(authority -> authority.replace("ROLE_", ""))
                .map(UserRole::valueOf)
                .orElseThrow();

        return role;
    }
}
