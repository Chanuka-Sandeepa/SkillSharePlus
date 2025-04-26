package com.example.skillshareplus.model;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.Id;
import org.springframework.data.annotation.LastModifiedDate;
import org.springframework.data.mongodb.core.index.Indexed;
import org.springframework.data.mongodb.core.mapping.Document;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import java.time.LocalDateTime;
import java.util.Collection;
import java.util.HashSet;
import java.util.Set;
import java.util.stream.Collectors;

@Builder
@NoArgsConstructor
@AllArgsConstructor
@Data
@Document(collection = "users")
public class User implements UserDetails {

    @Id
    private String id;

    @Indexed(unique = true)
    private String email;

    private String password;
    private String firstName;
    private String lastName;

    @Builder.Default
    private Set<Role> roles = new HashSet<>();

    // Set of user IDs that this user follows
    @Builder.Default
    private Set<String> following = new HashSet<>();
    
    // Set of user IDs that follow this user
    @Builder.Default
    private Set<String> followers = new HashSet<>();
    
    @Builder.Default
    private int followerCount = 0;
    
    @Builder.Default
    private int followingCount = 0;

    @Builder.Default
    private boolean enabled = true;

    @CreatedDate
    private LocalDateTime createdAt;

    @LastModifiedDate
    private LocalDateTime updatedAt;

    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        return roles.stream()
                .map(role -> new SimpleGrantedAuthority(role.name()))
                .collect(Collectors.toList());
    }

    @Override
    public String getUsername() {
        return email;
    }

    @Override
    public boolean isAccountNonExpired() {
        return true;
    }

    @Override
    public boolean isAccountNonLocked() {
        return true;
    }

    @Override
    public boolean isCredentialsNonExpired() {
        return true;
    }

    @Override
    public boolean isEnabled() {
        return enabled;
    }

    // Helper methods for followers/following management
    public void addFollower(String userId) {
        this.followers.add(userId);
        this.followerCount = this.followers.size();
    }

    public void removeFollower(String userId) {
        this.followers.remove(userId);
        this.followerCount = this.followers.size();
    }

    public void addFollowing(String userId) {
        this.following.add(userId);
        this.followingCount = this.following.size();
    }

    public void removeFollowing(String userId) {
        this.following.remove(userId);
        this.followingCount = this.following.size();
    }

    public boolean isFollowing(String userId) {
        return this.following.contains(userId);
    }

    public boolean isFollowedBy(String userId) {
        return this.followers.contains(userId);
    }
}
