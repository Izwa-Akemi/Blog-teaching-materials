package com.example.blog.controller.admin;

import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

import com.example.blog.config.CustomUserDetails;
import com.example.blog.service.PostService;

import lombok.RequiredArgsConstructor;

@Controller
@RequestMapping("/admin/posts")
@RequiredArgsConstructor
public class AdminPostListController {
	private final PostService postService;

    /** 一覧画面 */
    @GetMapping
    public String list(Model model) {

        model.addAttribute("posts", postService.findAll());
    

        return "admin/posts/index";
    }
}
