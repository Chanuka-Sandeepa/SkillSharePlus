package com.example.skillshareplus.controller;

import com.example.skillshareplus.dto.request.LearningPlanRequest;
import com.example.skillshareplus.dto.response.LearningPlanResponse;
import com.skillshareplus.dto.request.LearningPlanRequest;
import com.skillshareplus.dto.response.LearningPlanResponse;
import com.skillshareplus.security.services.LearningPlanService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import jakarta.validation.Valid;
import java.util.List;

@RestController
@RequestMapping("/api/learning-plans")
public class LearningPlanController {
    @Autowired
    private LearningPlanService learningPlanService;
    
    @PostMapping
    public ResponseEntity<LearningPlanResponse> createLearningPlan(@Valid @RequestBody LearningPlanRequest request) {
        LearningPlanResponse response = learningPlanService.createLearningPlan(request);
        return ResponseEntity.ok(response);
    }
    
    @GetMapping("/{id}")
    public ResponseEntity<LearningPlanResponse> getLearningPlanById(@PathVariable String id) {
        LearningPlanResponse response = learningPlanService.getLearningPlanById(id);
        return ResponseEntity.ok(response);
    }
    
    @GetMapping
    public ResponseEntity<List<LearningPlanResponse>> getUserLearningPlans() {
        List<LearningPlanResponse> responses = learningPlanService.getUserLearningPlans();
        return ResponseEntity.ok(responses);
    }
    
    @PutMapping("/{id}/progress")
    public ResponseEntity<LearningPlanResponse> updateProgress(
            @PathVariable String id,
            @Valid @RequestBody LearningPlanProgressRequest request) {
        LearningPlanResponse response = learningPlanService.updateProgress(id, request);
        return ResponseEntity.ok(response);
    }
    
    @PutMapping("/{id}")
    public ResponseEntity<LearningPlanResponse> updateLearningPlan(
            @PathVariable String id,
            @Valid @RequestBody LearningPlanRequest request) {
        LearningPlanResponse response = learningPlanService.updateLearningPlan(id, request);
        return ResponseEntity.ok(response);
    }
    
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteLearningPlan(@PathVariable String id) {
        learningPlanService.deleteLearningPlan(id);
        return ResponseEntity.noContent().build();
    }

}