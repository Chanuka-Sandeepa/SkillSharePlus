package com.example.skillshareplus.security.services;

import com.example.skillshareplus.exception.ResourceNotFoundException;
import com.example.skillshareplus.model.User;
import com.example.skillshareplus.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
@Slf4j
public class FollowService {

    private final UserRepository userRepository;

    @Transactional
    public void followUser(String currentUserId, String targetUserId) {
        log.info("Attempting to follow user: currentUserId={}, targetUserId={}", currentUserId, targetUserId);
        
        if (currentUserId.equals(targetUserId)) {
            throw new IllegalArgumentException("Users cannot follow themselves");
        }

        User currentUser = userRepository.findById(currentUserId)
                .orElseThrow(() -> new ResourceNotFoundException("Current user not found"));
        log.info("Found current user: {}", currentUser.getId());

        User targetUser = userRepository.findById(targetUserId)
                .orElseThrow(() -> new ResourceNotFoundException("Target user not found"));
        log.info("Found target user: {}", targetUser.getId());

        // Add the follow relationship
        currentUser.addFollowing(targetUserId);
        targetUser.addFollower(currentUserId);
        
        log.info("Updated following/followers. Current user following count: {}, Target user follower count: {}", 
                currentUser.getFollowingCount(), targetUser.getFollowerCount());

        // Save both users
        userRepository.save(currentUser);
        userRepository.save(targetUser);
        
        log.info("Saved users: Current user={}, Target user={}", currentUser, targetUser);
    }

    @Transactional
    public void unfollowUser(String currentUserId, String targetUserId) {
        log.info("Attempting to unfollow user: currentUserId={}, targetUserId={}", currentUserId, targetUserId);
        
        User currentUser = userRepository.findById(currentUserId)
                .orElseThrow(() -> new ResourceNotFoundException("Current user not found"));
        log.info("Found current user: {}", currentUser.getId());

        User targetUser = userRepository.findById(targetUserId)
                .orElseThrow(() -> new ResourceNotFoundException("Target user not found"));
        log.info("Found target user: {}", targetUser.getId());

        // Remove the follow relationship
        currentUser.removeFollowing(targetUserId);
        targetUser.removeFollower(currentUserId);
        
        log.info("Updated following/followers. Current user following count: {}, Target user follower count: {}", 
                currentUser.getFollowingCount(), targetUser.getFollowerCount());

        // Save both users
        userRepository.save(currentUser);
        userRepository.save(targetUser);
        
        log.info("Saved users: Current user={}, Target user={}", currentUser, targetUser);
    }

    public boolean isFollowing(String currentUserId, String targetUserId) {
        User currentUser = userRepository.findById(currentUserId)
                .orElseThrow(() -> new ResourceNotFoundException("Current user not found"));
        
        return currentUser.isFollowing(targetUserId);
    }
}
