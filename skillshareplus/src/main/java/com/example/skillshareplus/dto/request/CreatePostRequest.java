package com.example.skillshareplus.dto.request;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class CreatePostRequest {
    @Size(max = 500, message = "Description cannot exceed 500 characters")
    private String description;
    
    // Custom toString for debugging
    @Override
    public String toString() {
        return "CreatePostRequest{description='" + description + "'}";
    }
}