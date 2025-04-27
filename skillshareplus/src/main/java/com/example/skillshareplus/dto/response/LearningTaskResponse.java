package com.example.skillshareplus.dto.response;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class LearningTaskResponse {
    private String id;
    private String title;
    private String description;
    private List<ResourceResponse> resources;
    private int estimatedMinutes;
    private LocalDateTime completedAt;
}