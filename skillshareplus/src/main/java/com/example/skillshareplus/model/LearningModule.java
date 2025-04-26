package com.example.skillshareplus.model;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.ArrayList;
import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class LearningModule {
    private String id;
    private String title;
    private String description;
    private List<LearningTask> tasks = new ArrayList<>();
    private int estimatedHours;
    private int completedHours;
}