package com.example.skillshareplus.dto.request;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import jakarta.validation.constraints.NotBlank;
import java.util.ArrayList;
import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class LearningPlanRequest {
    @NotBlank(message = "Title is required")
    private String title;
    
    private String description;
    
    private List<LearningModuleRequest> modules = new ArrayList<>();
    
    private boolean isTemplate;
    
    private String category;
    
    private int estimatedHours;
}