package com.example.blog.dto;

import lombok.Builder;
import lombok.Data;

import java.time.LocalDateTime;

import com.example.blog.entity.role.Role;

@Data
@Builder
public class UserDto {

    private Long id;

    private String username;

    private String displayName;

    private Role role;

    private boolean enabled;

    private LocalDateTime createdAt;

    private LocalDateTime updatedAt;

    private Long postCount; // ユーザーが書いた記事数
}
