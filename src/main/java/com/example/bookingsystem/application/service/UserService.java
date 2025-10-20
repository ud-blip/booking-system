package com.example.bookingsystem.application.service;

import com.example.bookingsystem.application.exception.UserNotFoundException;
import com.example.bookingsystem.application.mapper.BookingMapper;
import com.example.bookingsystem.domain.User;
import com.example.bookingsystem.infrastructure.repository.UserRepository;
import com.example.bookingsystem.presentation.dto.UserProfileDTO;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

@Service
public class UserService {

    private final UserRepository userRepository;
    private final BookingMapper bookingMapper;

    public UserService(UserRepository userRepository, BookingMapper bookingMapper) {
        this.userRepository = userRepository;
        this.bookingMapper = bookingMapper;
    }

    public UserProfileDTO getProfile() {
        String username = SecurityContextHolder.getContext().getAuthentication().getName();
        User user = userRepository.findByUsername(username)
                .orElseThrow(() -> new UserNotFoundException("User not found: " + username));
        return bookingMapper.toUserProfileDTO(user);
    }

    public UserProfileDTO updateProfile(UserProfileDTO dto) {
        String username = SecurityContextHolder.getContext().getAuthentication().getName();
        User user = userRepository.findByUsername(username)
                .orElseThrow(() -> new UserNotFoundException("User not found: " + username));

        if (dto.getEmail() != null && !dto.getEmail().equals(user.getEmail())) {
            if (userRepository.existsByEmail(dto.getEmail())) {
                throw new IllegalArgumentException("Email already in use");
            }
            user.setEmail(dto.getEmail());
        }
        if (dto.getUsername() != null && !dto.getUsername().equals(user.getUsername())) {
            if (userRepository.existsByUsername(dto.getUsername())) {
                throw new IllegalArgumentException("Username already in use");
            }
            user.setUsername(dto.getUsername());
        }

        User updatedUser = userRepository.save(user);
        return bookingMapper.toUserProfileDTO(updatedUser);
    }
}
