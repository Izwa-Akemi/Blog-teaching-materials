package com.example.blog.controller.user;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;

import com.example.blog.service.CategoryService;
import com.example.blog.service.PostService;

import lombok.RequiredArgsConstructor;

@Controller
@RequiredArgsConstructor
public class UserBlogController {

	private final PostService postService;
	private final CategoryService categoryService;

	/* ユーザートップページ */
	@GetMapping("/blog")
	public String blogTop(
			@RequestParam(value = "category", required = false) Long categoryId,
			Model model) {

		if (categoryId != null) {
			model.addAttribute("posts", postService.findAll().stream()
					.filter(p -> p.getCategoryId().equals(categoryId))
					.toList());
		} else {
			model.addAttribute("posts", postService.findAll());
		}

		model.addAttribute("recentPosts", postService.findRecentPosts(3));
		model.addAttribute("categories", categoryService.findAll());
		model.addAttribute("categoryId", categoryId);

		return "user/blog/index";
	}
}