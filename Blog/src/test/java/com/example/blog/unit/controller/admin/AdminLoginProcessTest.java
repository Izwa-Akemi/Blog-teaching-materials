package com.example.blog.unit.controller.admin;

import static org.mockito.Mockito.when;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestBuilders.formLogin;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.redirectedUrl;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.test.web.servlet.MockMvc;

import com.example.blog.config.CustomUserDetails;
import com.example.blog.entity.User;
import com.example.blog.entity.role.Role;
import com.example.blog.service.CustomUserDetailsService;

@SpringBootTest
@AutoConfigureMockMvc
class AdminLoginProcessTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private CustomUserDetailsService userDetailsService; // ★ 管理者認証のモック

    @Autowired
    private BCryptPasswordEncoder encoder;

    @Test
    void 管理者ログイン成功() throws Exception {

        // --- テスト用管理者ユーザー作成 ---
        User admin = User.builder()
                .id(1L)
                .email("admin@test.com")
                .password(encoder.encode("password123"))
                .role(Role.ROLE_ADMIN)
                .enabled(true)
                .build();

        // UserDetailsService の戻り値を差し替え
        when(userDetailsService.loadUserByUsername("admin@test.com"))
                .thenReturn(new CustomUserDetails(admin));

        // ===== ログイン実行 =====
        mockMvc.perform(formLogin("/admin/login")
                        .user("email", "admin@test.com")
                        .password("password", "password123"))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/admin/dashboard")); // 期待通り！
    }

    @Test
    void 管理者ログイン失敗_パスワード間違い() throws Exception {

        // 正しいユーザー情報は返される（パスワード不一致のケース）
        User admin = User.builder()
                .id(1L)
                .email("admin@test.com")
                .password(encoder.encode("password123"))
                .role(Role.ROLE_ADMIN)
                .enabled(true)
                .build();

        when(userDetailsService.loadUserByUsername("admin@test.com"))
                .thenReturn(new CustomUserDetails(admin));

        // ===== 誤パスワードでログイン =====
        mockMvc.perform(formLogin("/admin/login")
                        .user("email", "admin@test.com")
                        .password("password", "wrongpass"))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/admin/login?error"));
    }
}