package com.example.blog.controller.user;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;

import com.example.blog.dto.PostDto;
import com.example.blog.service.CategoryService;
import com.example.blog.service.PostService;

import lombok.RequiredArgsConstructor;

@Controller
@RequiredArgsConstructor
public class UserBlogController {

    private final PostService postService;
    private final CategoryService categoryService;

    @GetMapping("/blog")
    public String blogTop(
            @RequestParam(value = "category", required = false) Long categoryId,
            @RequestParam(value = "keyword", required = false) String keyword,
            Model model) {

        // -----------------------------------------
        // ★ 検索条件いずれかが入っていたら検索モード
        // -----------------------------------------
        if ((categoryId != null) || (keyword != null && !keyword.isBlank())) {

            model.addAttribute("posts",
                    postService.searchPublished(categoryId, keyword));

        } else {
            // 通常一覧（公開済み）
            model.addAttribute("posts",
                    postService.findAllPublished());
        }

        // サイドバー用
        model.addAttribute("recentPosts",
                postService.findRecentPosts(3).stream()
                        .filter(PostDto::isPublished)
                        .toList());

        model.addAttribute("categories", categoryService.findAll());
        model.addAttribute("categoryId", categoryId);
        model.addAttribute("keyword", keyword);

        return "user/blog/index";
    }
}