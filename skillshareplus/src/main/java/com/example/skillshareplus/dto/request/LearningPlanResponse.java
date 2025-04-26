package com.example.skillshareplus.dto.request;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class LearningPlanResponse {
    private String id;
    private String title;
    private String description;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
    private List<LearningModuleResponse> modules;
    private boolean isTemplate;
    private String category;
    private int estimatedHours;
    private int completedHours;
}