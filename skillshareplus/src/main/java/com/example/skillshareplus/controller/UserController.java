package com.example.skillshareplus.controller;

import com.example.skillshareplus.dto.request.FollowRequest;
import com.example.skillshareplus.dto.request.UpdateProfileRequest;
import com.example.skillshareplus.dto.response.UserResponse;
import com.example.skillshareplus.exception.ResourceNotFoundException;
import com.example.skillshareplus.model.User;
import com.example.skillshareplus.repository.UserRepository;
import com.example.skillshareplus.security.services.FollowService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/users")
@RequiredArgsConstructor
@Slf4j
public class UserController {

    private final UserRepository userRepository;
    private final FollowService followService;

    // Get current user's profile
    @GetMapping("/profile")
    public ResponseEntity<?> getUserProfile(@AuthenticationPrincipal User currentUser) {
        return ResponseEntity.ok(UserResponse.fromUser(currentUser, currentUser));
    }

    // Update current user's profile
    @PutMapping("/profile")
    public ResponseEntity<?> updateUserProfile(
            @AuthenticationPrincipal User currentUser,
            @RequestBody UpdateProfileRequest updateRequest) {

        currentUser.setFirstName(updateRequest.getFirstName());
        currentUser.setLastName(updateRequest.getLastName());

        User updatedUser = userRepository.save(currentUser);
        return ResponseEntity.ok(UserResponse.fromUser(updatedUser, updatedUser));
    }

    // Follow a user
    @PostMapping("/follow")
    public ResponseEntity<?> followUser(
            @AuthenticationPrincipal User currentUser,
            @RequestBody FollowRequest followRequest) {

        followService.followUser(currentUser.getId(), followRequest.getUserId());
        return ResponseEntity.ok(Map.of("message", "Successfully followed user"));
    }

    // Unfollow a user
    @PostMapping("/unfollow")
    public ResponseEntity<?> unfollowUser(
            @AuthenticationPrincipal User currentUser,
            @RequestBody FollowRequest followRequest) {

        followService.unfollowUser(currentUser.getId(), followRequest.getUserId());
        return ResponseEntity.ok(Map.of("message", "Successfully unfollowed user"));
    }

    // Get user profile by ID
    @GetMapping("/{userId}")
    public ResponseEntity<?> getUserById(
            @AuthenticationPrincipal User currentUser,
            @PathVariable String userId) {

        return userRepository.findById(userId)
                .map(user -> ResponseEntity.ok(UserResponse.fromUser(user, currentUser)))
                .orElse(ResponseEntity.notFound().build());
    }

    // Get current user's followers
    @GetMapping("/followers")
    public ResponseEntity<?> getFollowers(@AuthenticationPrincipal User currentUser) {
        User refreshedUser = userRepository.findById(currentUser.getId())
                .orElseThrow(() -> new ResourceNotFoundException("User not found"));

        List<UserResponse> followers = userRepository.findAllById(refreshedUser.getFollowers())
                .stream()
                .map(user -> UserResponse.fromUser(user, refreshedUser))
                .collect(Collectors.toList());

        return ResponseEntity.ok(followers);
    }

    // Get current user's following
    @GetMapping("/following")
    public ResponseEntity<?> getFollowing(@AuthenticationPrincipal User currentUser) {
        User refreshedUser = userRepository.findById(currentUser.getId())
                .orElseThrow(() -> new ResourceNotFoundException("User not found"));

        List<UserResponse> following = userRepository.findAllById(refreshedUser.getFollowing())
                .stream()
                .map(user -> UserResponse.fromUser(user, refreshedUser))
                .collect(Collectors.toList());

        return ResponseEntity.ok(following);
    }

    // Get the followers of a user by userId
    @GetMapping("/{userId}/followers")
    public ResponseEntity<?> getUserFollowers(
            @AuthenticationPrincipal User currentUser,
            @PathVariable String userId) {

        return userRepository.findById(userId)
                .map(user -> {
                    List<UserResponse> followers = userRepository.findAllById(user.getFollowers())
                            .stream()
                            .map(follower -> UserResponse.fromUser(follower, currentUser))
                            .collect(Collectors.toList());
                    return ResponseEntity.ok(followers);
                })
                .orElse(ResponseEntity.notFound().build());
    }

    // Get the following list of a user by userId
    @GetMapping("/{userId}/following")
    public ResponseEntity<?> getUserFollowing(
            @AuthenticationPrincipal User currentUser,
            @PathVariable String userId) {

        return userRepository.findById(userId)
                .map(user -> {
                    List<UserResponse> following = userRepository.findAllById(user.getFollowing())
                            .stream()
                            .map(followedUser -> UserResponse.fromUser(followedUser, currentUser))
                            .collect(Collectors.toList());
                    return ResponseEntity.ok(following);
                })
                .orElse(ResponseEntity.notFound().build());
    }
}
