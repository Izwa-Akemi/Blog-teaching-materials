package com.example.blog.dto;

import lombok.Builder;
import lombok.Data;

import java.time.LocalDateTime;

@Data
@Builder
public class PostDto {

    private Long id;

    private String title;

    private String content;

    private String authorName;

    private Long authorId;

    private String categoryName;

    private Long categoryId;

    private boolean published;
    private String imagePath;

    private Long viewCount;

    private LocalDateTime createdAt;

    private LocalDateTime updatedAt;
   
}
