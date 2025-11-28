package com.example.blog.controller.admin;

import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

import com.example.blog.config.CustomUserDetails;
import com.example.blog.service.CategoryService;
import com.example.blog.service.PostService;

import lombok.RequiredArgsConstructor;

@Controller
@RequestMapping("/admin/posts")
@RequiredArgsConstructor
public class AdminPostListController {
	private final PostService postService;
	private final CategoryService categoryService;
    /** 一覧画面 */
	@GetMapping
	public String list(
	        @RequestParam(value = "category", required = false) Long categoryId,
	        @RequestParam(value = "keyword", required = false) String keyword,
	        Model model) {

	    // 検索条件あり
	    if ((categoryId != null) || (keyword != null && !keyword.isBlank())) {
	        model.addAttribute("posts",
	                postService.searchForAdmin(categoryId, keyword));
	    } else {
	        // 全件（管理者なので公開・下書き両方）
	        model.addAttribute("posts", postService.findAll());
	    }

	    // カテゴリ一覧も渡す（検索フォームに必要）
	    model.addAttribute("categories", categoryService.findAll());

	    // 入力値の保持
	    model.addAttribute("categoryId", categoryId);
	    model.addAttribute("keyword", keyword);

	    return "admin/posts/index";
	}
}
