package com.example.is.dto.response;

import com.example.is.entity.UserRole;

public record AuthResponse(Integer id, String username, UserRole role) {
}
