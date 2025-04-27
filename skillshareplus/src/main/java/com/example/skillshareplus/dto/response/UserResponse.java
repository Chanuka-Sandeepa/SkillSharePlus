package com.example.skillshareplus.dto.response;

import com.example.skillshareplus.model.User;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class UserResponse {
    private String id;
    private String email;
    private String firstName;
    private String lastName;
    private List<String> roles;
    private int followerCount;
    private int followingCount;
    private boolean isFollowing;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;

    public static UserResponse fromUser(User targetUser, User currentUser) {
        boolean isFollowing = currentUser != null && currentUser.isFollowing(targetUser.getId());
        return UserResponse.builder()
                .id(targetUser.getId())
                .email(targetUser.getEmail())
                .firstName(targetUser.getFirstName())
                .lastName(targetUser.getLastName())
                .followerCount(targetUser.getFollowerCount())
                .followingCount(targetUser.getFollowingCount())
                .roles(targetUser.getRoles().stream().map(Enum::name).collect(Collectors.toList()))
                .isFollowing(isFollowing)
                .createdAt(targetUser.getCreatedAt())
                .updatedAt(targetUser.getUpdatedAt())
                .build();
    }
}