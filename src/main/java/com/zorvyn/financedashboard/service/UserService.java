package com.zorvyn.financedashboard.service;

import com.zorvyn.financedashboard.dto.request.UpdateUserRoleRequest;
import com.zorvyn.financedashboard.dto.request.UpdateUserStatusRequest;
import com.zorvyn.financedashboard.dto.response.UserResponse;
import com.zorvyn.financedashboard.entity.User;
import com.zorvyn.financedashboard.exception.ResourceNotFoundException;
import com.zorvyn.financedashboard.exception.UnauthorizedException;
import com.zorvyn.financedashboard.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

import java.util.UUID;

@Service
@RequiredArgsConstructor
public class UserService {

    private final UserRepository userRepository;

    public Page<UserResponse> getAllUsers(Pageable pageable) {
        return userRepository.findAll(pageable)
                .map(UserResponse::from);
    }

    public UserResponse getUserById(UUID id) {
        User user = findUserById(id);
        return UserResponse.from(user);
    }

    public UserResponse getMyProfile() {
        User currentUser = getCurrentUser();
        return UserResponse.from(currentUser);
    }

    public UserResponse updateRole(UUID id, UpdateUserRoleRequest request) {
        User user = findUserById(id);

        // Prevent admin from demoting themselves
        User currentUser = getCurrentUser();
        if (currentUser.getId().equals(id)) {
            throw new UnauthorizedException(
                    "You cannot change your own role");
        }

        user.setRole(request.role());
        return UserResponse.from(userRepository.save(user));
    }

    public UserResponse updateStatus(UUID id, UpdateUserStatusRequest request) {
        User user = findUserById(id);

        // Prevent admin from deactivating themselves
        User currentUser = getCurrentUser();
        if (currentUser.getId().equals(id)) {
            throw new UnauthorizedException(
                    "You cannot change your own status");
        }

        user.setStatus(request.status());
        return UserResponse.from(userRepository.save(user));
    }


    private User findUserById(UUID id) {
        return userRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException(
                        "User not found with id: " + id));
    }

    private User getCurrentUser() {
        String email = SecurityContextHolder.getContext()
                .getAuthentication().getName();
        return userRepository.findByEmail(email)
                .orElseThrow(() -> new ResourceNotFoundException(
                        "Authenticated user not found"));
    }
}