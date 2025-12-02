package com.example.blog.unit.controller.admin;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.view;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.context.annotation.Import;
import org.springframework.test.web.servlet.MockMvc;

import com.example.blog.config.SecurityConfig;
import com.example.blog.controller.admin.AdminLoginController;

@WebMvcTest(AdminLoginController.class)
@Import(SecurityConfig.class)   // ★ SecurityFilterChain を読み込む
class AdminLoginControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Test
    void 管理者ログインページへアクセスできる() throws Exception {

        mockMvc.perform(get("/admin/login"))
            .andExpect(status().isOk())                      // HTTP 200
            .andExpect(view().name("admin/login"));          // View 名前
    }
}