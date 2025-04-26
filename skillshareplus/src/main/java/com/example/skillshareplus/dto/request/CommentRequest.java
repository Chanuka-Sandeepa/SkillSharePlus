package com.example.skillshareplus.dto.request;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class CommentRequest {

    @NotBlank(message = "Comment content cannot be empty")
    @Size(max = 200, message = "Comment cannot exceed 200 characters")
    private String content;
}
