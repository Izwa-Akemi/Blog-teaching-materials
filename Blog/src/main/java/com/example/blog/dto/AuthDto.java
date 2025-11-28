package com.example.blog.dto;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class AuthDto {

    private Long userId;

    private String username;

    private String displayName;

    private String role;
}
