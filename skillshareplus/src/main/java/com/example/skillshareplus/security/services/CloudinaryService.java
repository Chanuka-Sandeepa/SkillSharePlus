package com.example.skillshareplus.security.services;

import com.cloudinary.Cloudinary;
import com.cloudinary.utils.ObjectUtils;
import com.example.skillshareplus.model.Post;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.Map;

@Service
@RequiredArgsConstructor
@Slf4j
public class CloudinaryService {

    private final Cloudinary cloudinary;

    /**
     * Upload a file to Cloudinary
     * @param file The file to upload
     * @param isVideo Whether the file is a video
     * @return Media object containing the URL and type
     * @throws IOException If there is an issue with uploading
     */
    public Post.Media uploadFile(MultipartFile file, boolean isVideo) throws IOException {
        if (file == null || file.isEmpty()) {
            throw new IllegalArgumentException("File cannot be empty");
        }
        
        String resourceType = isVideo ? "video" : "image";
        log.info("Uploading file as type: {}, original content type: {}, size: {}", 
                 resourceType, file.getContentType(), file.getSize());
        
        try {
            Map uploadResult = cloudinary.uploader().upload(
                file.getBytes(),
                ObjectUtils.asMap(
                    "resource_type", resourceType,
                    "folder", "skillshare_posts"
                )
            );
            
            String url = (String) uploadResult.get("secure_url");
            String publicId = (String) uploadResult.get("public_id");
            
            if (url == null || publicId == null) {
                throw new IOException("Failed to get URL or public ID from Cloudinary");
            }
            
            Post.MediaType mediaType = isVideo ? Post.MediaType.VIDEO : Post.MediaType.PHOTO;
            log.info("File uploaded successfully to: {}", url);
            
            return new Post.Media(url, mediaType, publicId);
        } catch (Exception e) {
            log.error("Error uploading file to Cloudinary: {}", e.getMessage(), e);
            throw new IOException("Failed to upload file: " + e.getMessage(), e);
        }
    }
    
    /**
     * Delete a file from Cloudinary
     * @param publicId The public ID of the file
     * @param isVideo Whether the file is a video
     * @throws IOException If there is an issue with deletion
     */
    public void deleteFile(String publicId, boolean isVideo) throws IOException {
        if (publicId == null || publicId.isEmpty()) {
            log.warn("Attempted to delete file with null or empty publicId");
            return;
        }
        
        String resourceType = isVideo ? "video" : "image";
        log.info("Deleting file from Cloudinary: publicId={}, resourceType={}", publicId, resourceType);
        
        try {
            Map result = cloudinary.uploader().destroy(
                publicId,
                ObjectUtils.asMap("resource_type", resourceType)
            );
            
            String status = (String) result.get("result");
            if ("ok".equals(status)) {
                log.info("Successfully deleted file from Cloudinary: {}", publicId);
            } else {
                log.warn("Cloudinary deletion returned non-OK status: {}", status);
            }
        } catch (Exception e) {
            log.error("Error deleting file from Cloudinary: {}", e.getMessage(), e);
            throw new IOException("Failed to delete file: " + e.getMessage(), e);
        }
    }
}
