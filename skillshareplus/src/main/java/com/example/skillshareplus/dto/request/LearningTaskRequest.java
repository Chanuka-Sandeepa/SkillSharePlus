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
public class LearningTaskRequest {
    @NotBlank(message = "Task title is required")
    private String title;
    
    private String description;
    
    private List<ResourceRequest> resources = new ArrayList<>();
    
    private int estimatedMinutes;
}