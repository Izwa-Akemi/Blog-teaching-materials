package com.example.blog.unit.controller.admin;

import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ModelAttribute;

import com.example.blog.entity.User;

@ControllerAdvice
public class TestGlobalControllerAdvice {

    @ModelAttribute("currentUser")
    public User currentUser() {
        User user = new User();
        user.setId(1L);
        user.setDisplayName("Test Admin");
        user.setEmail("admin@test.com");
        return user;
    }

    @ModelAttribute("isLoggedIn")
    public boolean isLoggedIn() {
        return true;
    }

    @ModelAttribute("currentUserId")
    public Long currentUserId() {
        return 1L;
    }

    @ModelAttribute("currentUserName")
    public String currentUserName() {
        return "Test Admin";
    }
}
