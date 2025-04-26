package com.example.skillshareplus.controller;

import com.example.skillshareplus.dto.request.CommentRequest;
import com.example.skillshareplus.dto.request.CreatePostRequest;
import com.skillshareplus.dto.response.PagedPostsResponse;
import com.skillshareplus.dto.response.PostResponse;
import com.skillshareplus.security.services.PostService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.NoSuchElementException;

@RestController
@RequestMapping("/api/posts")
@RequiredArgsConstructor
@Slf4j
public class PostController {

    private final PostService postService;

    /**
     * Create a new post with media uploads
     * @param description Post description
     * @param files Up to 3 media files (photos or videos)
     * @return The created post
     */
    @PostMapping(consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<PostResponse> createPost(
            @RequestParam("description") String description,
            @RequestParam(value = "files", required = false) MultipartFile[] files) {
        
        log.info("Description received: {}", description);
        
        List<MultipartFile> filesList = new ArrayList<>();
        if (files != null) {
            filesList = Arrays.asList(files);
            log.info("Number of files received: {}", filesList.size());
            
            for (int i = 0; i < filesList.size(); i++) {
                MultipartFile file = filesList.get(i);
                if (file != null && !file.isEmpty()) {
                    log.info("File {}: name={}, contentType={}, size={}", 
                        i, file.getOriginalFilename(), file.getContentType(), file.getSize());
                } else {
                    log.warn("File {} is null or empty", i);
                }
            }
        } else {
            log.warn("No files received in request");
        }
        
        CreatePostRequest request = new CreatePostRequest(description);
        PostResponse createdPost = postService.createPost(request, filesList);
        
        log.info("Created post with {} media items", 
                createdPost.getMediaItems() != null ? createdPost.getMediaItems().size() : 0);
        
        return ResponseEntity.status(HttpStatus.CREATED).body(createdPost);
    }
    
    /**
     * Get a post by its ID
     * @param postId Post ID
     * @return The post
     */
    @GetMapping("/{postId}")
    public ResponseEntity<PostResponse> getPostById(@PathVariable String postId) {
        try {
            PostResponse post = postService.getPostById(postId);
            return ResponseEntity.ok(post);
        } catch (NoSuchElementException e) {
            return ResponseEntity.notFound().build();
        }
    }
    
    /**
     * Get posts with pagination
     * @param page Page number (default: 0)
     * @param size Page size (default: 10)
     * @return Paged posts
     */
    @GetMapping
    public ResponseEntity<PagedPostsResponse> getPosts(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size) {
        
        PagedPostsResponse pagedPosts = postService.getPosts(page, size);
        return ResponseEntity.ok(pagedPosts);
    }
    
    /**
     * Get posts by a specific user
     * @param userId User ID
     * @param page Page number (default: 0)
     * @param size Page size (default: 10)
     * @return Paged posts from the user
     */
    @GetMapping("/user/{userId}")
    public ResponseEntity<PagedPostsResponse> getPostsByUser(
            @PathVariable String userId,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size) {
        
        PagedPostsResponse pagedPosts = postService.getPostsByUser(userId, page, size);
        return ResponseEntity.ok(pagedPosts);
    }
    
    /**
     * Delete a post
     * @param postId Post ID
     * @return No content response
     */
    @DeleteMapping("/{postId}")
    public ResponseEntity<Void> deletePost(@PathVariable String postId) {
        try {
            postService.deletePost(postId);
            return ResponseEntity.noContent().build();
        } catch (NoSuchElementException e) {
            return ResponseEntity.notFound().build();
        } catch (IllegalStateException e) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN).build();
        }
    }
    
    /**
     * Like or unlike a post
     * @param postId Post ID
     * @return Updated post
     */
    @PostMapping("/{postId}/like")
    public ResponseEntity<PostResponse> toggleLike(@PathVariable String postId) {
        try {
            PostResponse updatedPost = postService.toggleLike(postId);
            return ResponseEntity.ok(updatedPost);
        } catch (NoSuchElementException e) {
            return ResponseEntity.notFound().build();
        }
    }
    
    /**
     * Add a comment to a post
     * @param postId Post ID
     * @param commentRequest Comment data
     * @return Updated post
     */
    @PostMapping("/{postId}/comments")
    public ResponseEntity<PostResponse> addComment(
            @PathVariable String postId,
            @Valid @RequestBody CommentRequest commentRequest) {
        
        try {
            PostResponse updatedPost = postService.addComment(postId, commentRequest);
            return ResponseEntity.status(HttpStatus.CREATED).body(updatedPost);
        } catch (NoSuchElementException e) {
            return ResponseEntity.notFound().build();
        }
    }
    
    /**
     * Delete a comment from a post
     * @param postId Post ID
     * @param commentId Comment ID
     * @return Updated post
     */
    @DeleteMapping("/{postId}/comments/{commentId}")
    public ResponseEntity<PostResponse> deleteComment(
            @PathVariable String postId,
            @PathVariable String commentId) {
        
        try {
            PostResponse updatedPost = postService.deleteComment(postId, commentId);
            return ResponseEntity.ok(updatedPost);
        } catch (NoSuchElementException e) {
            return ResponseEntity.notFound().build();
        } catch (IllegalStateException e) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN).build();
        }
    }
}