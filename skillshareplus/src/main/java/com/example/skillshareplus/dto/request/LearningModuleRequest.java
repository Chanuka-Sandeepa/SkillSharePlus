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
public class LearningModuleRequest {
    @NotBlank(message = "Module title is required")
    private String title;
    
    private String description;
    
    private List<LearningTaskRequest> tasks = new ArrayList<>();
    
    private int estimatedHours;
}