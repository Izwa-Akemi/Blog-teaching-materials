package com.example.blog.dto;

import lombok.Builder;
import lombok.Data;

import java.time.LocalDateTime;

@Data
@Builder
public class CommentDto {

    private Long id;

    private Long postId;

    private String postTitle;

    private Long userId;

    private String authorName;

    private String content;

    private boolean approved;

    private boolean deleted;

    private LocalDateTime createdAt;
}
