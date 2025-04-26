package com.example.skillshareplus.dto.request;

import com.example.skillshareplus.model.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class ResourceRequest {
    @NotBlank(message = "Resource title is required")
    private String title;
    
    private String url;
    
    @NotNull(message = "Resource type is required")
    private ResourceType type;
    
    private String notes;
}