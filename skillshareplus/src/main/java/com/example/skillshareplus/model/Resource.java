package com.example.skillshareplus.model;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class Resource {
    private String id;
    private String title;
    private String url;
    private ResourceType type;
    private String notes;
}
