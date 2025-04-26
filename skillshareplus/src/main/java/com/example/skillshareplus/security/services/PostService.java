package com.example.skillshareplus.security.services;

import com.example.skillshareplus.dto.request.CommentRequest;
import com.example.skillshareplus.dto.request.CreatePostRequest;
import com.example.skillshareplus.dto.response.PagedPostsResponse;
import com.example.skillshareplus.dto.response.PostResponse;
import com.example.skillshareplus.model.Post;
import com.example.skillshareplus.model.User;
import com.example.skillshareplus.repository.PostRepository;
import com.example.skillshareplus.repository.UserRepository;
import jakarta.validation.ValidationException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.time.LocalDateTime;
import java.util.*;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Slf4j
public class PostService {

    private final PostRepository postRepository;
    private final UserRepository userRepository;
    private final UserDetailsServiceImpl userDetailsService;
    private final CloudinaryService cloudinaryService;

    /**
     * Create a new post with media uploads
     * @param request Post description
     * @param mediaFiles Up to 3 media files (photos or videos)
     * @return The created post
     */
    @Transactional
    public PostResponse createPost(CreatePostRequest request, List<MultipartFile> mediaFiles) {
        // Get the current user ID
        String userId = userDetailsService.getId();
        if (userId == null) {
            throw new IllegalStateException("User not authenticated");
        }
        
        // Get user information
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new IllegalStateException("User not found"));
        
        // Create new post with proper checks
        Post post = new Post();
        post.setUserId(userId);
        post.setUsername(user.getUsername());
        post.setMainDescription(request.getDescription());
        post.setCreatedAt(LocalDateTime.now());
        post.setUpdatedAt(LocalDateTime.now());
        
        // Upload and set media files with better error handling
        List<Post.Media> mediaItems = new ArrayList<>();
        
        if (mediaFiles != null && !mediaFiles.isEmpty()) {
            // Filter out empty files
            List<MultipartFile> validFiles = mediaFiles.stream()
                    .filter(file -> file != null && !file.isEmpty())
                    .collect(Collectors.toList());
            
            log.info("Processing {} valid files for upload", validFiles.size());
            
            if (validFiles.size() > 3) {
                throw new ValidationException("Cannot upload more than 3 media files per post");
            }
            
            for (MultipartFile file : validFiles) {
                try {
                    // Better content type checking
                    String contentType = file.getContentType();
                    if (contentType == null) {
                        log.warn("File content type is null for file: {}", file.getOriginalFilename());
                        continue;
                    }
                    
                    boolean isVideo = contentType.startsWith("video/") || contentType.contains("video");
                    
                    // Upload file to Cloudinary with logging
                    log.info("Uploading file: name={}, type={}, size={}", 
                            file.getOriginalFilename(), contentType, file.getSize());
                    Post.Media media = cloudinaryService.uploadFile(file, isVideo);
                    
                    if (media != null && media.getUrl() != null && !media.getUrl().isEmpty()) {
                        mediaItems.add(media);
                        log.info("Media uploaded successfully: {}", media.getUrl());
                    } else {
                        log.warn("Media upload returned null or empty URL");
                    }
                } catch (IOException e) {
                    log.error("Failed to upload media file: {}", file.getOriginalFilename(), e);
                }
            }
        }
        
        post.setMediaItems(mediaItems);
        log.info("Setting {} media items on post", mediaItems.size());
        
        // Save post
        Post savedPost = postRepository.save(post);
        log.info("Post saved with ID: {}, Media items: {}", 
                savedPost.getId(), savedPost.getMediaItems().size());
        
        // Return response
        return PostResponse.fromEntity(savedPost, userId);
    }
    
    /**
     * Get a post by its ID
     * @param postId Post ID
     * @return The post
     */
    public PostResponse getPostById(String postId) {
        String currentUserId = userDetailsService.getId();
        Post post = postRepository.findById(postId)
                .orElseThrow(() -> new NoSuchElementException("Post not found with ID: " + postId));
        
        return PostResponse.fromEntity(post, currentUserId);
    }
    
    /**
     * Get posts with pagination
     * @param page Page number
     * @param size Page size
     * @return Paged posts
     */
    public PagedPostsResponse getPosts(int page, int size) {
        String currentUserId = userDetailsService.getId();
        
        Pageable pageable = PageRequest.of(page, size, Sort.by("createdAt").descending());
        Page<Post> postsPage = postRepository.findAllByOrderByCreatedAtDesc(pageable);
        
        List<PostResponse> postResponses = postsPage.getContent().stream()
                .map(post -> PostResponse.fromEntity(post, currentUserId))
                .collect(Collectors.toList());
        
        return new PagedPostsResponse(
                postResponses,
                postsPage.getNumber(),
                postsPage.getTotalPages(),
                postsPage.getTotalElements(),
                postsPage.hasNext(),
                postsPage.hasPrevious()
        );
    }
    
    /**
     * Get posts by a specific user
     * @param userId User ID
     * @param page Page number
     * @param size Page size
     * @return Paged posts from the user
     */
    public PagedPostsResponse getPostsByUser(String userId, int page, int size) {
        String currentUserId = userDetailsService.getId();
        
        Pageable pageable = PageRequest.of(page, size, Sort.by("createdAt").descending());
        Page<Post> postsPage = postRepository.findByUserId(userId, pageable);
        
        List<PostResponse> postResponses = postsPage.getContent().stream()
                .map(post -> PostResponse.fromEntity(post, currentUserId))
                .collect(Collectors.toList());
        
        return new PagedPostsResponse(
                postResponses,
                postsPage.getNumber(),
                postsPage.getTotalPages(),
                postsPage.getTotalElements(),
                postsPage.hasNext(),
                postsPage.hasPrevious()
        );
    }
    
    /**
     * Delete a post
     * @param postId Post ID
     */
    @Transactional
    public void deletePost(String postId) {
        String currentUserId = userDetailsService.getId();
        
        Post post = postRepository.findById(postId)
                .orElseThrow(() -> new NoSuchElementException("Post not found with ID: " + postId));
        
        // Check if the current user is the owner of the post
        if (!post.getUserId().equals(currentUserId)) {
            throw new IllegalStateException("You can only delete your own posts");
        }
        
        // Delete media files from Cloudinary
        for (Post.Media media : post.getMediaItems()) {
            try {
                boolean isVideo = media.getType() == Post.MediaType.VIDEO;
                cloudinaryService.
                deleteFile(media.getPublicId(), isVideo);
            } catch (IOException e) {
                log.error("Failed to delete media file", e);
                // Continue with deletion even if there's an error with Cloudinary
            }
        }
        
        // Delete post
        postRepository.deleteById(postId);
    }
    
    /**
     * Like or unlike a post
     * @param postId Post ID
     * @return Updated post
     */
    @Transactional
    public PostResponse toggleLike(String postId) {
        String currentUserId = userDetailsService.getId();
        
        Post post = postRepository.findById(postId)
                .orElseThrow(() -> new NoSuchElementException("Post not found with ID: " + postId));
        
        // Check if user already liked the post
        if (post.getLikedByUserIds().contains(currentUserId)) {
            // Unlike
            post.getLikedByUserIds().remove(currentUserId);
        } else {
            // Like
            post.getLikedByUserIds().add(currentUserId);
        }
        
        post.setUpdatedAt(LocalDateTime.now());
        Post updatedPost = postRepository.save(post);
        
        return PostResponse.fromEntity(updatedPost, currentUserId);
    }
    
    /**
     * Add a comment to a post
     * @param postId Post ID
     * @param commentRequest Comment data
     * @return Updated post
     */
    @Transactional
    public PostResponse addComment(String postId, CommentRequest commentRequest) {
        String currentUserId = userDetailsService.getId();
        
        Post post = postRepository.findById(postId)
                .orElseThrow(() -> new NoSuchElementException("Post not found with ID: " + postId));
        
        User user = userRepository.findById(currentUserId)
                .orElseThrow(() -> new IllegalStateException("User not found"));
        
        // Create and add comment
        Post.Comment comment = new Post.Comment(
                UUID.randomUUID().toString(),
                currentUserId,
                user.getUsername(),
                commentRequest.getContent(),
                LocalDateTime.now()
        );
        
        post.getComments().add(comment);
        post.setUpdatedAt(LocalDateTime.now());
        
        Post updatedPost = postRepository.save(post);
        
        return PostResponse.fromEntity(updatedPost, currentUserId);
    }
    
    /**
     * Delete a comment from a post
     * @param postId Post ID
     * @param commentId Comment ID
     * @return Updated post
     */
    @Transactional
    public PostResponse deleteComment(String postId, String commentId) {
        String currentUserId = userDetailsService.getId();
        
        Post post = postRepository.findById(postId)
                .orElseThrow(() -> new NoSuchElementException("Post not found with ID: " + postId));
        
        // Find the comment
        Optional<Post.Comment> commentOptional = post.getComments().stream()
                .filter(c -> c.getId().equals(commentId))
                .findFirst();
        
        if (commentOptional.isEmpty()) {
            throw new NoSuchElementException("Comment not found with ID: " + commentId);
        }
        
        Post.Comment comment = commentOptional.get();
        
        // Check if the current user is the owner of the comment or the post
        if (!comment.getUserId().equals(currentUserId) && !post.getUserId().equals(currentUserId)) {
            throw new IllegalStateException("You can only delete your own comments or comments on your own posts");
        }
        
        // Remove comment
        post.getComments().removeIf(c -> c.getId().equals(commentId));
        post.setUpdatedAt(LocalDateTime.now());
        
        Post updatedPost = postRepository.save(post);
        
        return PostResponse.fromEntity(updatedPost, currentUserId);
    }
}