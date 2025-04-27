package com.example.skillshareplus.dto.response;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class TemplateResponse {
    private String id;
    private String title;
    private String description;
    private String category;
    private int estimatedHours;
    private int moduleCount;
    private int taskCount;
}
