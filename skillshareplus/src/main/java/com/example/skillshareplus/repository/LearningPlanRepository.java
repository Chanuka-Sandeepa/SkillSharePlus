package com.example.skillshareplus.repository;

import com.example.skillshareplus.model.LearningPlan;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface LearningPlanRepository extends MongoRepository<LearningPlan, String> {
    List<LearningPlan> findByUserId(String userId);
    List<LearningPlan> findByIsTemplateTrue();
    List<LearningPlan> findByUserIdAndIsTemplate(String userId, boolean isTemplate);
}