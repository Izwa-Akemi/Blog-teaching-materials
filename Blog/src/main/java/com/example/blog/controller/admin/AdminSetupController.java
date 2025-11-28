
package com.example.blog.controller.admin;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;

import com.example.blog.entity.role.Role;
import com.example.blog.form.UserForm;
import com.example.blog.service.UserService;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;

@Controller
@RequiredArgsConstructor
@RequestMapping("/admin/setup")
public class AdminSetupController {

    private final UserService userService;

    /* 初回セットアップページ */
    /* 初回セットアップページ */
    @GetMapping
    public String setupForm(Model model) {

        if (userService.countUsers() > 0) {
            return "redirect:/admin/login";
        }

        model.addAttribute("userForm", new UserForm());
        return "admin/setup";
    }

    /* 管理者作成処理 */
    @PostMapping
    public String setupSubmit(
            @Valid @ModelAttribute("userForm") UserForm form,
            BindingResult result) {

        if (userService.countUsers() > 0) {
            return "redirect:/admin/login";
        }

        if (result.hasErrors()) {
            return "admin/setup";
        }

        // ★ role を強制的に ADMIN として作成
        userService.createAdmin(form);

        return "redirect:/admin/login";
    }
}