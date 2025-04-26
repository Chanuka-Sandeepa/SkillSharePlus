package com.example.skillshareplus.dto.response;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class CommentResponse {
    private String id;
    private String userId;
    private String username;
    private String content;
    private LocalDateTime createdAt;
}
