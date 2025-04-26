package com.example.skillshareplus.dto.response;

import com.example.skillshareplus.model.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class ResourceResponse {
    private String id;
    private String title;
    private String url;
    private ResourceType type;
    private String notes;
}