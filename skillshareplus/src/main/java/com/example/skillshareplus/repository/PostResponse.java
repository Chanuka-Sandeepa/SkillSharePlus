package com.example.skillshareplus.repository;
import java.util.Optional;

import com.example.skillshareplus.model.Post;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class PostResponse {

    private String id;
    private String userId;
    private String username;
    private String description;
    private List<MediaResponse> mediaItems;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
    private int likesCount;
    private boolean likedByCurrentUser;
    private List<CommentResponse> comments;

    public static PostResponse fromEntity(Post post, String currentUserId) {
        return PostResponse.builder()
                .id(post.getId())
                .userId(post.getUserId())
                .username(post.getUsername())
                .description(post.getMainDescription())
                .mediaItems(post.getMediaItems().stream()
                        .map(media -> new MediaResponse(media.getUrl(), media.getType().toString()))
                        .collect(Collectors.toList()))
                .createdAt(post.getCreatedAt())
                .updatedAt(post.getUpdatedAt())
                .likesCount(post.getLikedByUserIds().size())
                .likedByCurrentUser(post.getLikedByUserIds().contains(currentUserId))
                .comments(post.getComments().stream()
                        .map(comment -> new CommentResponse(
                                comment.getId(),
                                comment.getUserId(),
                                comment.getUsername(),
                                comment.getContent(),
                                comment.getCreatedAt()))
                        .collect(Collectors.toList()))
                .build();
    }
}
