package com.example.skillshareplus.dto.response;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class PagedPostsResponse {
    private List<PostResponse> posts;
    private int currentPage;
    private int totalPages;
    private long totalItems;
    private boolean hasNext;
    private boolean hasPrevious;
}
