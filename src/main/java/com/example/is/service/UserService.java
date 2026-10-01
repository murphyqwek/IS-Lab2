package com.example.is.service;

import com.example.is.entity.User;
import com.example.is.entity.UserRole;
import com.example.is.exception.UserAlreadyExistException;
import com.example.is.repository.UserRepository;
import jakarta.transaction.Transactional;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

@Service
public class UserService {
    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;

    public UserService(UserRepository userRepository, PasswordEncoder passwordEncoder) {
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
    }

    @Transactional
    public User registerNewUser(String username, String password) {
        if(userRepository.existsUserByUsername(username)) {
            throw new UserAlreadyExistException("Username is already in use");
        }

        User user = new User();

        user.setUsername(username);
        user.setPassword(passwordEncoder.encode(password));
        user.setRole(UserRole.USER);

        return userRepository.save(user);
    }
}
