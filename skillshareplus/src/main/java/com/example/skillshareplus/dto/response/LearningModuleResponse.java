package com.example.skillshareplus.dto.response;

import com.example.skillshareplus.model.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;
@Data
@NoArgsConstructor
@AllArgsConstructor
public class LearningModuleResponse {
    private String id;
    private String title;
    private String description;
    private List<LearningTaskResponse> tasks;
    private int estimatedHours;
    private int completedHours;
}