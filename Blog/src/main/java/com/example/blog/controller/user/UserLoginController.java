package com.example.blog.controller.user;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class UserLoginController {
	  /* ログインページ */
    @GetMapping("/login")
    public String login(Model model) {
        return "user/auth/login";  // login.html を返す
    }
}
