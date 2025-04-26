package com.example.skillshareplus.model;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import java.time.LocalDateTime;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Document(collection = "media")
public class Media {
    
    @Id
    private String id;
    
    private String url;
    private String publicId; // For Cloudinary reference
    private MediaType type;
    private String description;
    private String fileName;
    private String mimeType;
    private Long fileSize;
    private LocalDateTime uploadedAt;
    private String uploadedBy;
    
    public enum MediaType {
        PHOTO,
        VIDEO
    }
    
    // Validation method
    public boolean isValid() {
        return url != null && 
               type != null && 
               description != null && 
               fileName != null && 
               mimeType != null && 
               fileSize != null && 
               fileSize > 0;
    }
} 