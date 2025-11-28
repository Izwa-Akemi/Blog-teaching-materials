package com.example.blog.controller.admin;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;

import com.example.blog.service.CommentService;

import lombok.RequiredArgsConstructor;

@Controller
@RequiredArgsConstructor
@RequestMapping("/admin/comments")
public class AdminCommentController {

    private final CommentService commentService;

    /* 一覧 */
    @GetMapping
    public String index(Model model) {
        model.addAttribute("comments", commentService.findAllForAdmin());
        return "admin/comment/index";
    }

    /* 承認切り替え */
    @PostMapping("/{id}/toggle")
    public String toggle(@PathVariable Long id) {
        commentService.toggleApproval(id);
        return "redirect:/admin/comments";
    }

    /* 削除 */
    @PostMapping("/{id}/delete")
    public String delete(@PathVariable Long id) {
        commentService.adminDelete(id);
        return "redirect:/admin/comments";
    }
}