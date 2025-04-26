package com.example.skillshareplus.repository;

import com.example.skillshareplus.model.Post;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface PostRepository extends MongoRepository<Post, String> {
    
    // Find posts by user ID
    List<Post> findByUserId(String userId);
    
    // Find posts by user ID with pagination
    Page<Post> findByUserId(String userId, Pageable pageable);
    
    // Find all posts with pagination (for feed)
    Page<Post> findAllByOrderByCreatedAtDesc(Pageable pageable);
}