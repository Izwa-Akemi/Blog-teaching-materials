package com.example.blog.controller.user;

import java.util.List;

import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import com.example.blog.config.CustomUserDetails;
import com.example.blog.dto.CommentDto;
import com.example.blog.form.CommentForm;
import com.example.blog.service.CommentService;
import com.example.blog.service.PostService;

import lombok.RequiredArgsConstructor;

@Controller
@RequiredArgsConstructor
@RequestMapping("/blog")
public class UserBlogDetailController {
	private final PostService postService;
    private final CommentService commentService;

    /* 記事詳細ページ */
    @GetMapping("/post/{id}")
    public String postDetail(
            @PathVariable Long id,
            @RequestParam(defaultValue = "0") int page,
            Model model,
            Authentication auth
    ) {

        model.addAttribute("post", postService.findById(id));
        model.addAttribute("recentPosts", postService.findRecentPosts(3));

        // Pagination -------------------------------------
        int size = 5;
        List<CommentDto> comments = commentService.findByPostId(id, page, size);
        long totalComments = commentService.countByPostId(id);
        int totalPages = (int) Math.ceil((double) totalComments / size);

        model.addAttribute("comments", comments);
        model.addAttribute("currentPage", page);
        model.addAttribute("totalPages", totalPages);

        // Login info -------------------------------------
        boolean isLoggedIn = (auth != null && auth.isAuthenticated()
                && !"anonymousUser".equals(auth.getPrincipal()));

        Long currentUserId = null;
        if (isLoggedIn) {
            CustomUserDetails user = (CustomUserDetails) auth.getPrincipal();
            currentUserId = user.getUser().getId();
        }

        model.addAttribute("isLoggedIn", isLoggedIn);
        model.addAttribute("currentUserId", currentUserId);

        return "user/blog/detail";
    }

    @PostMapping("/post/{id}/comment")
    public String addComment(
            @PathVariable Long id,
            @RequestParam String content,
            Authentication auth,
            RedirectAttributes redirect
    ) {
        if (auth == null || auth.getPrincipal().equals("anonymousUser")) {
            return "redirect:/login?redirectTo=/blog/post/" + id;
        }

        CustomUserDetails user = (CustomUserDetails) auth.getPrincipal();

        CommentForm form = new CommentForm();
        form.setPostId(id);
        form.setContent(content);

        commentService.create(form, user.getUser().getId());

        redirect.addFlashAttribute("msg", "コメントを投稿しました！");

        return "redirect:/blog/post/" + id;
    }
    @PostMapping("/post/{postId}/comment/{commentId}/delete")
    public String deleteComment(
            @PathVariable Long postId,
            @PathVariable Long commentId,
            Authentication auth,
            RedirectAttributes redirect
    ) {
        if (auth == null || auth.getPrincipal().equals("anonymousUser")) {
            return "redirect:/login?redirectTo=/blog/post/" + postId;
        }

        CustomUserDetails user = (CustomUserDetails) auth.getPrincipal();

        commentService.deleteByUser(commentId, user.getUser().getId());

        redirect.addFlashAttribute("msg", "コメントを削除しました");

        return "redirect:/blog/post/" + postId;
    }
    @PostMapping("/post/{postId}/comment/{commentId}/edit")
    public String editComment(
            @PathVariable Long postId,
            @PathVariable Long commentId,
            @RequestParam String content,
            Authentication auth,
            RedirectAttributes redirect
    ) {
        if (auth == null || auth.getPrincipal().equals("anonymousUser")) {
            return "redirect:/login?redirectTo=/blog/post/" + postId;
        }

        CustomUserDetails user = (CustomUserDetails) auth.getPrincipal();

        commentService.updateByUser(commentId, user.getUser().getId(), content);

        redirect.addFlashAttribute("msg", "コメントを編集しました");

        return "redirect:/blog/post/" + postId;
    }


}
