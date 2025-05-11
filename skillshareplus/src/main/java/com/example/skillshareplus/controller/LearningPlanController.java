package com.example.skillshareplus.controller;

import com.example.skillshareplus.dto.request.LearningPlanRequest;
import com.example.skillshareplus.dto.request.LearningPlanProgressRequest;
import com.example.skillshareplus.dto.response.LearningPlanResponse;
import com.example.skillshareplus.security.services.LearningPlanService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import jakarta.validation.Valid;
import java.util.List;

/**
 * Controller for managing learning plans in the application.
 * Provides endpoints for creating, retrieving, updating, and deleting learning plans,
 * as well as tracking progress within learning plans.
 */
@RestController
@RequestMapping("/api/learning-plans")
public class LearningPlanController {
    @Autowired
    private LearningPlanService learningPlanService;
    
    /**
     * Creates a new learning plan for the authenticated user.
     * @param request The learning plan details including title, description, and modules
     * @return The created learning plan with its ID and other details
     */
    @PostMapping
    public ResponseEntity<LearningPlanResponse> createLearningPlan(@Valid @RequestBody LearningPlanRequest request) {
        LearningPlanResponse response = learningPlanService.createLearningPlan(request);
        return ResponseEntity.ok(response);
    }
    
    /**
     * Retrieves a specific learning plan by its ID.
     * @param id The unique identifier of the learning plan
     * @return The learning plan details if found
     */
    @GetMapping("/{id}")
    public ResponseEntity<LearningPlanResponse> getLearningPlanById(@PathVariable String id) {
        LearningPlanResponse response = learningPlanService.getLearningPlanById(id);
        return ResponseEntity.ok(response);
    }
    
    /**
     * Retrieves all learning plans for the authenticated user.
     * @return List of learning plans associated with the current user
     */
    @GetMapping
    public ResponseEntity<List<LearningPlanResponse>> getUserLearningPlans() {
        List<LearningPlanResponse> responses = learningPlanService.getUserLearningPlans();
        return ResponseEntity.ok(responses);
    }
    
    /**
     * Updates the progress of a specific learning plan.
     * @param id The unique identifier of the learning plan
     * @param request The progress update details
     * @return The updated learning plan with new progress information
     */
    @PutMapping("/{id}/progress")
    public ResponseEntity<LearningPlanResponse> updateProgress(
            @PathVariable String id,
            @Valid @RequestBody LearningPlanProgressRequest request) {
        LearningPlanResponse response = learningPlanService.updateProgress(id, request);
        return ResponseEntity.ok(response);
    }
    
    /**
     * Updates an existing learning plan's details.
     * @param id The unique identifier of the learning plan
     * @param request The updated learning plan details
     * @return The updated learning plan
     */
    @PutMapping("/{id}")
    public ResponseEntity<LearningPlanResponse> updateLearningPlan(
            @PathVariable String id,
            @Valid @RequestBody LearningPlanRequest request) {
        LearningPlanResponse response = learningPlanService.updateLearningPlan(id, request);
        return ResponseEntity.ok(response);
    }
    
    /**
     * Deletes a learning plan.
     * @param id The unique identifier of the learning plan to delete
     * @return No content response if deletion is successful
     */
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteLearningPlan(@PathVariable String id) {
        learningPlanService.deleteLearningPlan(id);
        return ResponseEntity.noContent().build();
    }
}