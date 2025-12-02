package com.example.blog.unit.controller.admin;

import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.redirectedUrlPattern;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.view;

import java.util.Collections;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.web.servlet.MockMvc;

import com.example.blog.service.CategoryService;
import com.example.blog.service.CommentService;
import com.example.blog.service.PostService;
import com.example.blog.service.UserService;

@SpringBootTest
@AutoConfigureMockMvc
@TestPropertySource(properties = {
	    "spring.main.allow-bean-definition-overriding=true"
	})
class AdminDashboardControllerTest {

    @Autowired
    private MockMvc mockMvc;

    // --- ダッシュボードで使うサービス類を Mock 化 ---
    @MockBean private PostService postService;
    @MockBean private UserService userService;
    @MockBean private CategoryService categoryService;
    @MockBean private CommentService commentService;

    /* =====================================================
       1. ADMINユーザーなら 200 OK でアクセス成功
       ===================================================== */
    @Test
    @WithMockUser(username = "admin@test.com", roles = {"ADMIN"})
    void 管理者はダッシュボードにアクセスできる() throws Exception {

        // --- ダッシュボードに必要な値をモックしておく ---
        when(postService.countPosts()).thenReturn(10L);
        when(postService.countPublished()).thenReturn(8L);
        when(postService.countDraft()).thenReturn(2L);
        when(postService.findRecentPosts(3)).thenReturn(Collections.emptyList());
        when(postService.getMonthlyPostStats()).thenReturn(Collections.emptyList());

        when(categoryService.getCategoryNames()).thenReturn(Collections.emptyList());
        when(categoryService.getCategoryPostCounts()).thenReturn(Collections.emptyList());
        when(categoryService.countCategories()).thenReturn(5L);

        when(userService.countAdmins()).thenReturn(1L);
        when(userService.countGeneralUsers()).thenReturn(100L);
        when(userService.findRecentUsers(3)).thenReturn(Collections.emptyList());

        when(commentService.countComments()).thenReturn(50L);
        when(commentService.findRecentComments(5)).thenReturn(Collections.emptyList());

        // --- 実行 ---
        mockMvc.perform(get("/admin/dashboard"))
                .andExpect(status().isOk())                // 200 OK
                .andExpect(view().name("admin/dashboard"));// 正しいテンプレート
    }

    /* =====================================================
       2. 一般ユーザー（ROLE_USER）は 403 Forbidden
       ===================================================== */
    @Test
    @WithMockUser(username = "user@test.com", roles = {"USER"})
    void 一般ユーザーはアクセス拒否される() throws Exception {

        mockMvc.perform(get("/admin/dashboard"))
                .andExpect(status().isForbidden()); // 403
    }

    /* =====================================================
       3. 未ログインは /admin/login へリダイレクト
       ===================================================== */
    @Test
    void 未ログインはログインページへリダイレクト() throws Exception {

        mockMvc.perform(get("/admin/dashboard"))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrlPattern("**/admin/login"));
    }
}