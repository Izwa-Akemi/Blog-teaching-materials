package com.example.blog.controller.admin;

import java.util.List;
import java.util.Map;

import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

import com.example.blog.config.CustomUserDetails;
import com.example.blog.entity.User;
import com.example.blog.service.CategoryService;
import com.example.blog.service.CommentService;
import com.example.blog.service.PostService;
import com.example.blog.service.UserService;
import com.fasterxml.jackson.databind.ObjectMapper;

import lombok.RequiredArgsConstructor;

@Controller
@RequestMapping("/admin")
@RequiredArgsConstructor
public class AdminDashboardController {

    private final PostService postService;
    private final UserService userService;
    private final CategoryService categoryService;
    private final CommentService commentService;

    private final ObjectMapper objectMapper = new ObjectMapper();

    @GetMapping("/dashboard")
    public String dashboard(Model model) {

        // --- 投稿関連 ---
        model.addAttribute("postCount", postService.countPosts());
        model.addAttribute("publishedCount", postService.countPublished());
        model.addAttribute("draftCount", postService.countDraft());
        model.addAttribute("recentPosts", postService.findRecentPosts(3));

        // 月別投稿数（折れ線グラフ用）
        // --- 月別投稿数(折れ線グラフ用) ---
        try {
            List<Map<String, Object>> stats = postService.getMonthlyPostStats();
            model.addAttribute("postStatsJson", objectMapper.writeValueAsString(stats));
        } catch (Exception e) {
            model.addAttribute("postStatsJson", "[]");
        }

        // カテゴリ割合（円グラフ）
        model.addAttribute("categoryLabels", categoryService.getCategoryNames());
        model.addAttribute("categoryCounts", categoryService.getCategoryPostCounts());

        // --- ユーザー関連 ---
        model.addAttribute("adminCount", userService.countAdmins());
        model.addAttribute("userCount", userService.countGeneralUsers());
        model.addAttribute("recentUsers", userService.findRecentUsers(3));

        // --- コメント関連 ---
        model.addAttribute("commentCount", commentService.countComments());
        model.addAttribute("recentComments", commentService.findRecentComments(5));

        // --- カテゴリー関連 ---
        model.addAttribute("categoryCount", categoryService.countCategories());

        return "admin/dashboard";
    }
}