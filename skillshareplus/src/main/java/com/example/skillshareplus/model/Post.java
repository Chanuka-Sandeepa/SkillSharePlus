package com.example.skillshareplus.model;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Document(collection = "posts")
public class Post {
    
    @Id
    private String id;
    
    private String userId;
    private String username; // Store username for easier display
    
    private String mainDescription;
    
    // List of exactly three media items
    private List<Media> mediaItems = new ArrayList<>();
    
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
    
    // Engagement metrics
    private List<String> likedByUserIds = new ArrayList<>();
    private List<Comment> comments = new ArrayList<>();
    
    // Media class to differentiate between photos and videos
    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    public static class Media {
        private String url;
        private MediaType type;
        private String publicId; // For Cloudinary reference
        
        public boolean isValid() {
            return url != null && !url.isEmpty();
        }
    }
    
    // Comment class
    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    public static class Comment {
        private String id;
        private String userId;
        private String username;
        private String content;
        private LocalDateTime createdAt;
    }
    
    public enum MediaType {
        PHOTO,
        VIDEO
    }
    
    // Validation method for media items
    public boolean isValidMediaItems() {
        if (mediaItems == null || mediaItems.size() != 3) {
            return false;
        }
        
        return mediaItems.stream().allMatch(media -> 
            media != null && 
            media.isValid() && 
            (media.getType() == Post.MediaType.PHOTO || media.getType() == Post.MediaType.VIDEO)
        );
    }
}