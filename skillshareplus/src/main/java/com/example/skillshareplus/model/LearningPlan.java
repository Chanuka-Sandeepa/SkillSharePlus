package com.SkillShare.SkillShare.model;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Document(collection = "learningPlans")
public class LearningPlan {
    @Id
    private String id;
    private String title;
    private String description;
    private String userId;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
    private List<LearningModule> modules = new ArrayList<>();
    private boolean isTemplate;
    private String category;
    private int estimatedHours;
    private int completedHours;
}
