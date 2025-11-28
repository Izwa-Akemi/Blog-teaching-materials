package com.example.blog.controller.user;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;

import com.example.blog.form.UserForm;
import com.example.blog.service.UserService;

import lombok.RequiredArgsConstructor;

@Controller
@RequiredArgsConstructor
public class UserRegisterController {

    private final UserService userService;

    /* --- サインアップ画面 --- */
    @GetMapping("/signup")
    public String signupForm(Model model) {
        model.addAttribute("userForm", new UserForm());
        return "user/auth/signup";
    }

    /* --- サインアップ処理 --- */
    @PostMapping("/signup")
    public String signupSubmit(@ModelAttribute UserForm form, Model model) {
        try {
            userService.registerUser(form);
        } catch (Exception e) {
            model.addAttribute("error", e.getMessage());
            return "user/auth/signup";
        }

        // 登録完了 → ログイン画面へ
        return "redirect:/login?registered";
    }
}