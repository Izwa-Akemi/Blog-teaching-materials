package com.example.blog.form;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

@Data
public class CommentForm {

    @NotNull
    private Long postId;

    @NotBlank(message = "コメント内容を入力してください")
    private String content;
}