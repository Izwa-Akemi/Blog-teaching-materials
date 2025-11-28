package com.example.blog.controller.admin;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class AdminLoginController {
	  /* ログインページ */
    @GetMapping("/admin/login")
    public String login(Model model) {
        return "admin/login";  // login.html を返す
    }
}
