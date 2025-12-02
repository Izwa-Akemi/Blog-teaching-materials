package com.example.blog.unit.controller.admin;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.flash;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.model;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.redirectedUrl;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.view;

import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.servlet.MockMvc;

import com.example.blog.form.UserForm;
import com.example.blog.service.UserService;

@SpringBootTest
@AutoConfigureMockMvc
class AdminRegisterControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private UserService userService;

    /* =====================================================
       1. GET: 新規作成フォームが表示される
       ===================================================== */
    @Test
    @WithMockUser(username = "admin@test.com", roles = {"ADMIN"})
    void 新規作成フォームが表示される() throws Exception {
        mockMvc.perform(get("/admin/users/create"))
                .andExpect(status().isOk())
                .andExpect(view().name("admin/users/create"))
                .andExpect(model().attributeExists("userForm"));
    }

    /* =====================================================
       2. POST: バリデーションエラー時はフォームに戻る
       ===================================================== */
    @Test
    @WithMockUser(username = "admin@test.com", roles = {"ADMIN"})
    void バリデーションエラーの場合_フォームに戻る() throws Exception {

        // 必須項目欠け → Validation エラーを起こす
        mockMvc.perform(post("/admin/users/create")
                .param("email", "") // 空なのでエラー
                .param("password", "") // 空なのでエラー
                .param("displayName", "")) // 空なのでエラー
                .andExpect(status().isOk())
                .andExpect(view().name("admin/users/create"))
                .andExpect(model().hasErrors());

        verify(userService, never()).createAdmin(org.mockito.ArgumentMatchers.any());
    }

    /* =====================================================
       3. POST: 正常に管理者が作成される
       ===================================================== */
    @Test
    @WithMockUser(username = "admin@test.com", roles = {"ADMIN"})
    void 正常に管理者が作成されリダイレクト_引数検証あり() throws Exception {

        mockMvc.perform(post("/admin/users/create")
                .with(csrf())
                .param("email", "newadmin@test.com")
                .param("username", "admin02")
                .param("displayName", "New Admin")
                .param("password", "password123"))
            .andExpect(status().is3xxRedirection())
            .andExpect(redirectedUrl("/admin/dashboard"))
            .andExpect(flash().attribute("success", "管理者を作成しました"));

        // --- ★ UserForm の引数キャプチャ ---
        ArgumentCaptor<UserForm> captor = ArgumentCaptor.forClass(UserForm.class);

        verify(userService, times(1)).createAdmin(captor.capture());

        UserForm captured = captor.getValue();

        // --- ★ 引数の内容を検証 ---
        org.assertj.core.api.Assertions.assertThat(captured.getEmail()).isEqualTo("newadmin@test.com");
        org.assertj.core.api.Assertions.assertThat(captured.getUsername()).isEqualTo("admin02");
        org.assertj.core.api.Assertions.assertThat(captured.getDisplayName()).isEqualTo("New Admin");
        org.assertj.core.api.Assertions.assertThat(captured.getPassword()).isEqualTo("password123");

        // enabled はデフォルトの true が入る
        org.assertj.core.api.Assertions.assertThat(captured.isEnabled()).isTrue();
    }

}