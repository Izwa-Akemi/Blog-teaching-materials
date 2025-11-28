package com.example.blog.controller.admin;

import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.multipart.MultipartFile;

import com.example.blog.config.CustomUserDetails;
import com.example.blog.form.PostForm;
import com.example.blog.service.CategoryService;
import com.example.blog.service.PostService;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;

@Controller
@RequestMapping("/admin/posts")
@RequiredArgsConstructor
public class AdminPostRegisterController {

    private final PostService postService;
    private final CategoryService categoryService;

    /* 新規作成フォーム */
    @GetMapping("/create")
    public String createForm(Model model) {
        model.addAttribute("postForm", new PostForm());
        model.addAttribute("categories", categoryService.findAll());

        return "admin/posts/create";
    }

    /* 新規作成処理 */
    @PostMapping("/create")
    public String create(
            @Valid @ModelAttribute("postForm") PostForm form,
            BindingResult result,
            @AuthenticationPrincipal CustomUserDetails userDetails,
            @RequestParam("imageFile") MultipartFile imageFile,   // ★追加！
            Model model) {

        if (result.hasErrors()) {
            model.addAttribute("categories", categoryService.findAll());
            return "admin/posts/create";
        }

        // ★ 画像付きで作成
        postService.create(form, userDetails.getUser().getId(), imageFile);

        return "redirect:/admin/posts?created";
    }
}