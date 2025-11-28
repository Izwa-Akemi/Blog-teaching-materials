package com.example.blog.form;

import com.example.blog.entity.role.Role;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

@Data
public class UserForm {

    private Long id;
    @NotBlank
    private String email;  // ★ 追加
    
    @NotBlank(message = "ユーザー名を入力してください")
    private String username;

    @NotBlank(message = "表示名を入力してください")
    private String displayName;

    @NotBlank(message = "パスワードを入力してください")
    private String password;

    private boolean enabled = true;
}