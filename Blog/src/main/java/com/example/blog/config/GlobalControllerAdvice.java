package com.example.blog.config;

import com.example.blog.entity.User;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Component;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ModelAttribute;

@Component
@ControllerAdvice
public class GlobalControllerAdvice {

    @ModelAttribute("currentUser")
    public User addCurrentUser(Authentication authentication) {

        if (authentication == null || !authentication.isAuthenticated()) {
            return null; // 未ログイン
        }

        Object principal = authentication.getPrincipal();

        if (principal instanceof CustomUserDetails userDetails) {
            return userDetails.getUser(); // ★ UserEntity を返す
        }

        return null;
    }
    /* ログインしているかどうかの真偽値 */
    @ModelAttribute("isLoggedIn")
    public boolean isLoggedIn(Authentication auth) {
        return auth != null && auth.isAuthenticated()
                && !"anonymousUser".equals(auth.getPrincipal());
    }

    /* ログインユーザーID */
    @ModelAttribute("currentUserId")
    public Long currentUserId(Authentication auth) {
        if (auth == null || !auth.isAuthenticated() ||
           auth.getPrincipal().equals("anonymousUser")) {
            return null;
        }
        CustomUserDetails user = (CustomUserDetails) auth.getPrincipal();
        return user.getUser().getId();
    }

    /* ログインユーザー名（表示名） */
    @ModelAttribute("currentUserName")
    public String currentUserName(Authentication auth) {

        if (auth == null || !auth.isAuthenticated() ||
           auth.getPrincipal().equals("anonymousUser")) {
            return null;
        }

        CustomUserDetails user = (CustomUserDetails) auth.getPrincipal();
        return user.getUser().getDisplayName();
    }
}