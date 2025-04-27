package com.example.skillshareplus.model;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class LearningTask {
    private String id;
    private String title;
    private String description;
    private List<Resource> resources = new ArrayList<>();
    private int estimatedMinutes;
    private LocalDateTime completedAt;
}