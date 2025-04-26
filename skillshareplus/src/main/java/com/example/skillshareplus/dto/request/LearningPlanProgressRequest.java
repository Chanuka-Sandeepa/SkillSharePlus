package com.example.skillshareplus.dto.request;

import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class LearningPlanProgressRequest {

    @NotBlank(message = "Module ID is required")
    private String moduleId;

    @NotBlank(message = "Task ID is required")
    private String taskId;
}
