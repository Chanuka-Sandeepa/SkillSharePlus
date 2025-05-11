package com.example.skillshareplus.security.services;

import com.example.skillshareplus.model.*;
import com.example.skillshareplus.dto.request.LearningModuleRequest;
import com.example.skillshareplus.dto.request.LearningPlanProgressRequest;
import com.example.skillshareplus.dto.request.LearningPlanRequest;
import com.example.skillshareplus.dto.request.LearningTaskRequest;
import com.example.skillshareplus.dto.request.ResourceRequest;
import com.example.skillshareplus.dto.response.LearningModuleResponse;
import com.example.skillshareplus.dto.response.LearningPlanResponse;
import com.example.skillshareplus.dto.response.LearningTaskResponse;
import com.example.skillshareplus.dto.response.ResourceResponse;
import com.example.skillshareplus.dto.response.TemplateResponse;
import com.example.skillshareplus.repository.LearningPlanRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.*;
import java.util.stream.Collectors;

@Service
public class LearningPlanService {

    @Autowired
    private LearningPlanRepository learningPlanRepository;

    public LearningPlanResponse createLearningPlan(LearningPlanRequest request) {
        // Get authenticated user
        User user = (User) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        String userId = user.getId();
        
        LearningPlan plan = new LearningPlan();
        plan.setTitle(request.getTitle());
        plan.setDescription(request.getDescription());
        plan.setUserId(userId);
        plan.setCreatedAt(LocalDateTime.now());
        plan.setUpdatedAt(LocalDateTime.now());
        plan.setTemplate(request.isTemplate());
        plan.setCategory(request.getCategory());
        plan.setEstimatedHours(request.getEstimatedHours());
        plan.setCompletedHours(0);
        
        // Process modules
        List<LearningModule> modules = new ArrayList<>();
        if (request.getModules() != null) {
            for (LearningModuleRequest moduleRequest : request.getModules()) {
                LearningModule module = new LearningModule();
                module.setId(UUID.randomUUID().toString());
                module.setTitle(moduleRequest.getTitle());
                module.setDescription(moduleRequest.getDescription());
                module.setEstimatedHours(moduleRequest.getEstimatedHours());
                module.setCompletedHours(0);
                
                // Process tasks
                List<LearningTask> tasks = new ArrayList<>();
                if (moduleRequest.getTasks() != null) {
                    for (LearningTaskRequest taskRequest : moduleRequest.getTasks()) {
                        LearningTask task = new LearningTask();
                        task.setId(UUID.randomUUID().toString());
                        task.setTitle(taskRequest.getTitle());
                        task.setDescription(taskRequest.getDescription());
                        task.setEstimatedMinutes(taskRequest.getEstimatedMinutes());
                        
                        // Process resources
                        List<Resource> resources = new ArrayList<>();
                        if (taskRequest.getResources() != null) {
                            for (ResourceRequest resourceRequest : taskRequest.getResources()) {
                                Resource resource = new Resource();
                                resource.setId(UUID.randomUUID().toString());
                                resource.setTitle(resourceRequest.getTitle());
                                resource.setUrl(resourceRequest.getUrl());
                                resource.setType(resourceRequest.getType());
                                resource.setNotes(resourceRequest.getNotes());
                                resources.add(resource);
                            }
                        }
                        task.setResources(resources);
                        tasks.add(task);
                    }
                }
                module.setTasks(tasks);
                modules.add(module);
            }
        }
        plan.setModules(modules);
        
        LearningPlan savedPlan = learningPlanRepository.save(plan);
        return convertToResponse(savedPlan);
    }

    public LearningPlanResponse getLearningPlanById(String id) {
        // Get authenticated user
        User user = (User) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        String userId = user.getId();
        
        Optional<LearningPlan> planOptional = learningPlanRepository.findById(id);
        if (planOptional.isPresent()) {
            LearningPlan plan = planOptional.get();
            // Allow access to templates by everyone
            if (plan.isTemplate() || plan.getUserId().equals(userId)) {
                return convertToResponse(plan);
            } else {
                throw new RuntimeException("Not authorized to access this learning plan");
            }
        } else {
            throw new RuntimeException("Learning plan not found");
        }
    }

    public List<LearningPlanResponse> getUserLearningPlans() {
        // Get authenticated user
        User user = (User) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        String userId = user.getId();
        
        List<LearningPlan> plans = learningPlanRepository.findByUserIdAndIsTemplate(userId, false);
        return plans.stream()
                .map(this::convertToResponse)
                .collect(Collectors.toList());
    }

    public List<TemplateResponse> getTemplates() {
        List<LearningPlan> templates = learningPlanRepository.findByIsTemplateTrue();
        
        return templates.stream()
                .map(plan -> {
                    TemplateResponse response = new TemplateResponse();
                    response.setId(plan.getId());
                    response.setTitle(plan.getTitle());
                    response.setDescription(plan.getDescription());
                    response.setCategory(plan.getCategory());
                    response.setEstimatedHours(plan.getEstimatedHours());
                    response.setModuleCount(plan.getModules().size());
                    
                    int taskCount = plan.getModules().stream()
                            .mapToInt(module -> module.getTasks().size())
                            .sum();
                    response.setTaskCount(taskCount);
                    
                    return response;
                })
                .collect(Collectors.toList());
    }

    public LearningPlanResponse createPlanFromTemplate(String templateId) {
        // Get authenticated user
        User user = (User) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        String userId = user.getId();
        
        Optional<LearningPlan> templateOptional = learningPlanRepository.findById(templateId);
        if (templateOptional.isPresent() && templateOptional.get().isTemplate()) {
            LearningPlan template = templateOptional.get();
            LearningPlan newPlan = new LearningPlan();
            
            // Copy basic info
            newPlan.setTitle(template.getTitle());
            newPlan.setDescription(template.getDescription());
            newPlan.setUserId(userId);
            newPlan.setCreatedAt(LocalDateTime.now());
            newPlan.setUpdatedAt(LocalDateTime.now());
            newPlan.setTemplate(false);
            newPlan.setCategory(template.getCategory());
            newPlan.setEstimatedHours(template.getEstimatedHours());
            newPlan.setCompletedHours(0);
            
            // Deep copy modules, tasks, and resources with new IDs
            List<LearningModule> newModules = new ArrayList<>();
            for (LearningModule templateModule : template.getModules()) {
                LearningModule newModule = new LearningModule();
                newModule.setId(UUID.randomUUID().toString());
                newModule.setTitle(templateModule.getTitle());
                newModule.setDescription(templateModule.getDescription());
                newModule.setEstimatedHours(templateModule.getEstimatedHours());
                newModule.setCompletedHours(0);
                
                List<LearningTask> newTasks = new ArrayList<>();
                for (LearningTask templateTask : templateModule.getTasks()) {
                    LearningTask newTask = new LearningTask();
                    newTask.setId(UUID.randomUUID().toString());
                    newTask.setTitle(templateTask.getTitle());
                    newTask.setDescription(templateTask.getDescription());
                    newTask.setEstimatedMinutes(templateTask.getEstimatedMinutes());
                    
                    List<Resource> newResources = new ArrayList<>();
                    for (Resource templateResource : templateTask.getResources()) {
                        Resource newResource = new Resource();
                        newResource.setId(UUID.randomUUID().toString());
                        newResource.setTitle(templateResource.getTitle());
                        newResource.setUrl(templateResource.getUrl());
                        newResource.setType(templateResource.getType());
                        newResource.setNotes(templateResource.getNotes());
                        newResources.add(newResource);
                    }
                    newTask.setResources(newResources);
                    newTasks.add(newTask);
                }
                newModule.setTasks(newTasks);
                newModules.add(newModule);
            }
            newPlan.setModules(newModules);
            
            LearningPlan savedPlan = learningPlanRepository.save(newPlan);
            return convertToResponse(savedPlan);
        } else {
            throw new RuntimeException("Template not found");
        }
    }

    public LearningPlanResponse updateProgress(String planId, LearningPlanProgressRequest request) {
        // Get authenticated user
        User user = (User) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        String userId = user.getId();
        
        Optional<LearningPlan> planOptional = learningPlanRepository.findById(planId);
        if (planOptional.isPresent()) {
            LearningPlan plan = planOptional.get();
            
            if (!plan.getUserId().equals(userId)) {
                throw new RuntimeException("Not authorized to update this learning plan");
            }
            
            boolean moduleFound = false;
            boolean taskFound = false;
            
            for (LearningModule module : plan.getModules()) {
                if (module.getId().equals(request.getModuleId())) {
                    moduleFound = true;
                    
                    for (LearningTask task : module.getTasks()) {
                        if (task.getId().equals(request.getTaskId())) {
                            taskFound = true;
                            task.setCompletedAt(LocalDateTime.now());
                            break;
                        }
                    }
                    
                    // Update module completed hours
                    updateModuleHours(module);
                    break;
                }
            }
            
            if (!moduleFound) {
                throw new RuntimeException("Module not found");
            }
            
            if (!taskFound) {
                throw new RuntimeException("Task not found");
            }
            
            // Update overall plan completed hours
            updatePlanHours(plan);
            
            plan.setUpdatedAt(LocalDateTime.now());
            LearningPlan savedPlan = learningPlanRepository.save(plan);
            return convertToResponse(savedPlan);
        } else {
            throw new RuntimeException("Learning plan not found");
        }
    }

    private void updateModuleHours(LearningModule module) {
        if (module.getTasks() == null || module.getTasks().isEmpty()) {
            module.setCompletedHours(0);
            return;
        }

        // Calculate completed hours
        int totalCompletedMinutes = module.getTasks().stream()
                .filter(task -> task.getCompletedAt() != null)
                .mapToInt(LearningTask::getEstimatedMinutes)
                .sum();
        
        module.setCompletedHours(totalCompletedMinutes / 60);
    }

    private void updatePlanHours(LearningPlan plan) {
        if (plan.getModules() == null || plan.getModules().isEmpty()) {
            plan.setCompletedHours(0);
            return;
        }
        
        // Calculate completed hours
        int totalCompletedHours = plan.getModules().stream()
                .mapToInt(LearningModule::getCompletedHours)
                .sum();
        
        plan.setCompletedHours(totalCompletedHours);
    }

    public LearningPlanResponse updateLearningPlan(String planId, LearningPlanRequest request) {
        // Get authenticated user
        User user = (User) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        String userId = user.getId();
        
        Optional<LearningPlan> planOptional = learningPlanRepository.findById(planId);
        if (planOptional.isPresent()) {
            LearningPlan plan = planOptional.get();
            
            if (!plan.getUserId().equals(userId)) {
                throw new RuntimeException("Not authorized to update this learning plan");
            }
            
            plan.setTitle(request.getTitle());
            plan.setDescription(request.getDescription());
            plan.setCategory(request.getCategory());
            plan.setEstimatedHours(request.getEstimatedHours());
            plan.setUpdatedAt(LocalDateTime.now());
            
            // For simplicity, we're not updating the full structure (modules, tasks) in this method
            // In a real application, you might want to handle this more carefully
            
            LearningPlan savedPlan = learningPlanRepository.save(plan);
            return convertToResponse(savedPlan);
        } else {
            throw new RuntimeException("Learning plan not found");
        }
    }

    public void deleteLearningPlan(String planId) {
        // Get authenticated user
        User user = (User) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        String userId = user.getId();
        
        Optional<LearningPlan> planOptional = learningPlanRepository.findById(planId);
        if (planOptional.isPresent()) {
            LearningPlan plan = planOptional.get();
            
            if (!plan.getUserId().equals(userId)) {
                throw new RuntimeException("Not authorized to delete this learning plan");
            }
            
            learningPlanRepository.delete(plan);
        } else {
            throw new RuntimeException("Learning plan not found");
        }
    }

    private LearningPlanResponse convertToResponse(LearningPlan plan) {
        LearningPlanResponse response = new LearningPlanResponse();
        response.setId(plan.getId());
        response.setTitle(plan.getTitle());
        response.setDescription(plan.getDescription());
        response.setCreatedAt(plan.getCreatedAt());
        response.setUpdatedAt(plan.getUpdatedAt());
        response.setCompletedHours(plan.getCompletedHours());
        
        List<LearningModuleResponse> moduleResponses = new ArrayList<>();
        for (LearningModule module : plan.getModules()) {
            LearningModuleResponse moduleResponse = new LearningModuleResponse();
            moduleResponse.setId(module.getId());
            moduleResponse.setTitle(module.getTitle());
            moduleResponse.setDescription(module.getDescription());
            moduleResponse.setEstimatedHours(module.getEstimatedHours());
            moduleResponse.setCompletedHours(module.getCompletedHours());
            
            List<LearningTaskResponse> taskResponses = new ArrayList<>();
            for (LearningTask task : module.getTasks()) {
                LearningTaskResponse taskResponse = new LearningTaskResponse();
                taskResponse.setId(task.getId());
                taskResponse.setTitle(task.getTitle());
                taskResponse.setDescription(task.getDescription());
                taskResponse.setEstimatedMinutes(task.getEstimatedMinutes());
                taskResponse.setCompletedAt(task.getCompletedAt());
                
                List<ResourceResponse> resourceResponses = new ArrayList<>();
                for (Resource resource : task.getResources()) {
                    ResourceResponse resourceResponse = new ResourceResponse();
                    resourceResponse.setId(resource.getId());
                    resourceResponse.setTitle(resource.getTitle());
                    resourceResponse.setUrl(resource.getUrl());
                    resourceResponse.setType(resource.getType());
                    resourceResponse.setNotes(resource.getNotes());
                    resourceResponses.add(resourceResponse);
                }
                
                taskResponse.setResources(resourceResponses);
                taskResponses.add(taskResponse);
            }
            
            moduleResponse.setTasks(taskResponses);
            moduleResponses.add(moduleResponse);
        }
        
        response.setModules(moduleResponses);
        return response;
    }
    
    // Method to create predefined templates
    public void createPredefinedTemplates() {
        // Check if templates already exist
        List<LearningPlan> existingTemplates = learningPlanRepository.findByIsTemplateTrue();
        if (!existingTemplates.isEmpty()) {
            return; // Templates already exist
        }
        
        // Create Java Development Template
        LearningPlan javaPlan = new LearningPlan();
        javaPlan.setTitle("Java Development Learning Path");
        javaPlan.setDescription("A comprehensive path to learn Java development from basics to advanced topics");
        javaPlan.setUserId("system");
        javaPlan.setCreatedAt(LocalDateTime.now());
        javaPlan.setUpdatedAt(LocalDateTime.now());
        javaPlan.setTemplate(true);
        javaPlan.setCategory("Programming");
        javaPlan.setEstimatedHours(120);
        
        // Module 1: Java Basics
        LearningModule javaBasics = new LearningModule();
        javaBasics.setId(UUID.randomUUID().toString());
        javaBasics.setTitle("Java Basics");
        javaBasics.setDescription("Learn the fundamentals of Java programming");
        javaBasics.setEstimatedHours(20);
        
        List<LearningTask> javaBasicsTasks = new ArrayList<>();
        
        LearningTask task1 = new LearningTask();
        task1.setId(UUID.randomUUID().toString());
        task1.setTitle("Java Syntax and Structure");
        task1.setDescription("Learn about Java syntax, variables, and basic operations");
        task1.setEstimatedMinutes(120);
        
        List<Resource> task1Resources = new ArrayList<>();
        task1Resources.add(new Resource(UUID.randomUUID().toString(), 
                "Java Programming for Beginners", 
                "https://example.com/java-beginners", 
                ResourceType.ARTICLE, 
                "Good introduction to Java syntax"));
        task1.setResources(task1Resources);
        
        javaBasicsTasks.add(task1);
        
        LearningTask task2 = new LearningTask();
        task2.setId(UUID.randomUUID().toString());
        task2.setTitle("Control Flow in Java");
        task2.setDescription("Learn about if-else statements, loops, and switch statements");
        task2.setEstimatedMinutes(180);
        
        List<Resource> task2Resources = new ArrayList<>();
        task2Resources.add(new Resource(UUID.randomUUID().toString(), 
                "Control Flow in Java", 
                "https://example.com/java-control-flow", 
                ResourceType.VIDEO, 
                "Complete tutorial on control flow statements"));
        task2.setResources(task2Resources);
        
        javaBasicsTasks.add(task2);
        
        javaBasics.setTasks(javaBasicsTasks);
        
        // Module 2: Object-Oriented Programming
        LearningModule oopModule = new LearningModule();
        oopModule.setId(UUID.randomUUID().toString());
        oopModule.setTitle("Object-Oriented Programming");
        oopModule.setDescription("Learn OOP principles in Java");
        oopModule.setEstimatedHours(25);
        
        List<LearningTask> oopTasks = new ArrayList<>();
        
        LearningTask oopTask1 = new LearningTask();
        oopTask1.setId(UUID.randomUUID().toString());
        oopTask1.setTitle("Classes and Objects");
        oopTask1.setDescription("Learn how to create and use classes and objects in Java");
        oopTask1.setEstimatedMinutes(240);
        
        List<Resource> oopTask1Resources = new ArrayList<>();
        oopTask1Resources.add(new Resource(UUID.randomUUID().toString(), 
                "Java Classes and Objects", 
                "https://example.com/java-classes", 
                ResourceType.VIDEO, 
                "Detailed explanation of classes and objects"));
        oopTask1.setResources(oopTask1Resources);
        
        oopTasks.add(oopTask1);
        
        LearningTask oopTask2 = new LearningTask();
        oopTask2.setId(UUID.randomUUID().toString());
        oopTask2.setTitle("Inheritance and Polymorphism");
        oopTask2.setDescription("Learn about inheritance, interfaces, and polymorphism");
        oopTask2.setEstimatedMinutes(300);
        
        List<Resource> oopTask2Resources = new ArrayList<>();
        oopTask2Resources.add(new Resource(UUID.randomUUID().toString(), 
                "Java Inheritance Tutorial", 
                "https://example.com/java-inheritance", 
                ResourceType.ARTICLE, 
                "Comprehensive guide to inheritance"));
        oopTask2Resources.add(new Resource(UUID.randomUUID().toString(), 
                "Polymorphism in Java", 
                "https://example.com/java-polymorphism", 
                ResourceType.VIDEO, 
                "Video tutorial on polymorphism"));
        oopTask2.setResources(oopTask2Resources);
        
        oopTasks.add(oopTask2);
        
        oopModule.setTasks(oopTasks);
        
        // Add modules to Java plan
        List<LearningModule> javaModules = new ArrayList<>();
        javaModules.add(javaBasics);
        javaModules.add(oopModule);
        javaPlan.setModules(javaModules);
        
        // Create Spring Boot Template
        LearningPlan springPlan = new LearningPlan();
        springPlan.setTitle("Spring Boot Development Path");
        springPlan.setDescription("Learn Spring Boot framework for building enterprise applications");
        springPlan.setUserId("system");
        springPlan.setCreatedAt(LocalDateTime.now());
        springPlan.setUpdatedAt(LocalDateTime.now());
        springPlan.setTemplate(true);
        springPlan.setCategory("Web Development");
        springPlan.setEstimatedHours(80);
        
        // Module 1: Spring Boot Basics
        LearningModule springBasics = new LearningModule();
        springBasics.setId(UUID.randomUUID().toString());
        springBasics.setTitle("Spring Boot Fundamentals");
        springBasics.setDescription("Learn the fundamentals of Spring Boot framework");
        springBasics.setEstimatedHours(15);
        
        List<LearningTask> springBasicsTasks = new ArrayList<>();
        
        LearningTask springTask1 = new LearningTask();
        springTask1.setId(UUID.randomUUID().toString());
        springTask1.setTitle("Spring Boot Introduction");
        springTask1.setDescription("Introduction to Spring Boot framework and its benefits");
        springTask1.setEstimatedMinutes(180);
        
        List<Resource> springTask1Resources = new ArrayList<>();
        springTask1Resources.add(new Resource(UUID.randomUUID().toString(), 
                "Spring Boot Introduction", 
                "https://example.com/spring-boot-intro", 
                ResourceType.ARTICLE, 
                "Official Spring Boot documentation"));
        springTask1.setResources(springTask1Resources);
        
        springBasicsTasks.add(springTask1);
        
        LearningTask springTask2 = new LearningTask();
        springTask2.setId(UUID.randomUUID().toString());
        springTask2.setTitle("Creating RESTful APIs");
        springTask2.setDescription("Learn how to create RESTful APIs with Spring Boot");
        springTask2.setEstimatedMinutes(240);
        
        List<Resource> springTask2Resources = new ArrayList<>();
        springTask2Resources.add(new Resource(UUID.randomUUID().toString(), 
                "Building a RESTful Web Service", 
                "https://example.com/spring-boot-rest", 
                ResourceType.EXERCISE, 
                "Hands-on tutorial for building RESTful APIs"));
        springTask2.setResources(springTask2Resources);
        
        springBasicsTasks.add(springTask2);
        
        springBasics.setTasks(springBasicsTasks);
        
        // Add modules to Spring plan
        List<LearningModule> springModules = new ArrayList<>();
        springModules.add(springBasics);
        springPlan.setModules(springModules);
        
        // Save templates
        learningPlanRepository.save(javaPlan);
        learningPlanRepository.save(springPlan);
    }

    /**
     * Retrieves learning plans by their status for the current user.
     * @param status The status to filter learning plans by
     * @return List of learning plans matching the specified status
     */
    public List<LearningPlanResponse> getLearningPlansByStatus(String status) {
        String userId = SecurityContextHolder.getContext().getAuthentication().getName();
        return learningPlanRepository.findByUserIdAndStatus(userId, status)
                .stream()
                .map(this::convertToResponse)
                .collect(Collectors.toList());
    }
}