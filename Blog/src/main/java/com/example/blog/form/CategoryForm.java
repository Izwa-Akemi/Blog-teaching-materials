package com.example.blog.form;



import jakarta.validation.constraints.NotBlank;
import lombok.Data;

@Data
public class CategoryForm {

    private Long id;

    @NotBlank(message = "カテゴリー名を入力してください")
    private String name;

    @NotBlank(message = "URLスラッグを入力してください")
    private String slug;
}