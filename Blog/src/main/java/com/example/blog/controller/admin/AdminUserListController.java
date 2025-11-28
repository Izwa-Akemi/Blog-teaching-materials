package com.example.blog.controller.admin;

import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

import com.example.blog.config.CustomUserDetails;
import com.example.blog.service.UserService;

import lombok.RequiredArgsConstructor;

@Controller
@RequestMapping("/admin/users")
@RequiredArgsConstructor
public class AdminUserListController {

    private final UserService userService;
	 /* 一覧表示 */
    @GetMapping("/list")
    public String index(Model model) {
        model.addAttribute("admins", userService.findAllAdmins());
        return "admin/users/index";
    }
}
