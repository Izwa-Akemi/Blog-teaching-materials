package com.example.blog.unit.controller.admin;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.ArgumentMatchers.nullable;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.multipart;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.model;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.redirectedUrl;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.redirectedUrlPattern;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.view;

import java.time.LocalDateTime;
import java.util.Collections;

import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.web.multipart.MultipartFile;

import com.example.blog.dto.PostDto;
import com.example.blog.form.PostForm;
import com.example.blog.service.CategoryService;
import com.example.blog.service.PostService;

import jakarta.servlet.ServletException;

@SpringBootTest
@AutoConfigureMockMvc
@TestPropertySource(properties = {
		"spring.main.allow-bean-definition-overriding=true"
})
class AdminPostEditControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private PostService postService;

    @MockBean
    private CategoryService categoryService;

    // 共通ダミー DTO
    private PostDto dummyPost() {
        return PostDto.builder()
                .id(1L)
                .title("Test Title")
                .content("Test Content")
                .categoryId(5L)
                .categoryName("Tech")
                .authorName("Admin")
                .imagePath("/uploads/test.jpg")
                .published(true)
                .createdAt(LocalDateTime.now())
                .build();
    }

    /* =====================================================
       1. 編集フォーム表示（GET）正常系
       ===================================================== */
    @Test
    @WithMockUser(username = "admin@test.com", roles = {"ADMIN"})
    void 編集画面が正しく表示される() throws Exception {

        when(postService.findById(1L)).thenReturn(dummyPost());
        when(categoryService.findAll()).thenReturn(Collections.emptyList());

        mockMvc.perform(get("/admin/posts/edit/1"))
                .andExpect(status().isOk())
                .andExpect(view().name("admin/posts/edit"))
                .andExpect(model().attributeExists("postForm"))
                .andExpect(model().attributeExists("categories"))
                .andExpect(model().attribute("postImage", "/uploads/test.jpg"))
                .andExpect(model().attribute("authorName", "Admin"));
    }


    /* =====================================================
       2. 存在しないIDの場合 → 500 エラー
       ===================================================== */
    /* =====================================================
    2. 存在しないIDの場合 → 例外が投げられることを確認
    ===================================================== */
 @Test
 @WithMockUser(roles = {"ADMIN"})
 void 存在しないIDの場合エラー() throws Exception {

     when(postService.findById(999L))
             .thenThrow(new RuntimeException("記事が見つかりません"));

     assertThatThrownBy(() -> mockMvc.perform(get("/admin/posts/edit/999")))
             .isInstanceOf(ServletException.class)                 // 外側
             .hasRootCauseInstanceOf(RuntimeException.class)       // 内側
             .hasRootCauseMessage("記事が見つかりません");
 }


    /* =====================================================
       3. バリデーションエラー（title 空）
       ===================================================== */
    @Test
    @WithMockUser(roles = {"ADMIN"})
    void タイトル空ならバリデーションエラー() throws Exception {

        when(categoryService.findAll()).thenReturn(Collections.emptyList());

        mockMvc.perform(post("/admin/posts/edit/1")
                .with(csrf())
                .param("title", "")
                .param("content", "Content")
                .param("categoryId", "5")
                .param("published", "true")
        )
                .andExpect(status().isOk())
                .andExpect(view().name("admin/posts/edit"))
                .andExpect(model().hasErrors());
    }


    /* =====================================================
       4. categoryId null → バリデーションエラー
       ===================================================== */
    @Test
    @WithMockUser(roles = {"ADMIN"})
    void カテゴリー未選択ならエラー() throws Exception {

        when(categoryService.findAll()).thenReturn(Collections.emptyList());

        mockMvc.perform(post("/admin/posts/edit/1")
                .with(csrf())
                .param("title", "Title")
                .param("content", "Content")
        )
                .andExpect(status().isOk())
                .andExpect(view().name("admin/posts/edit"))
                .andExpect(model().hasErrors());
    }


    /* =====================================================
       5. 画像なしで正常更新
       ===================================================== */
    @Test
    @WithMockUser(roles = {"ADMIN"})
    void 画像なしで正常更新できる() throws Exception {

        mockMvc.perform(post("/admin/posts/edit/1")
                .with(csrf())
                .param("id", "1")
                .param("title", "Updated Title")
                .param("content", "Updated Content")
                .param("categoryId", "5")
                .param("published", "true")
        )
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/admin/posts?updated"));

        ArgumentCaptor<Long> idCaptor = ArgumentCaptor.forClass(Long.class);
        ArgumentCaptor<PostForm> formCaptor = ArgumentCaptor.forClass(PostForm.class);
        ArgumentCaptor<MultipartFile> fileCaptor = ArgumentCaptor.forClass(MultipartFile.class);

        verify(postService).update(idCaptor.capture(), formCaptor.capture(), fileCaptor.capture());

        assertThat(idCaptor.getValue()).isEqualTo(1L);
        assertThat(fileCaptor.getValue()).isNull();   // ← null を確認
    }


    /* =====================================================
       6. 画像ありで正常更新（前回作成済み）
       ===================================================== */
    @Test
    @WithMockUser(roles = {"ADMIN"})
    void 画像付きで正常に更新できる() throws Exception {

        MockMultipartFile mockImage =
                new MockMultipartFile("imageFile", "test.jpg", "image/jpeg",
                        "dummy image content".getBytes());

        mockMvc.perform(multipart("/admin/posts/edit/1")
                .file(mockImage)
                .with(csrf())
                .param("id", "1")
                .param("title", "Updated Title With Image")
                .param("content", "Updated Content With Image")
                .param("categoryId", "3")
                .param("published", "true")
        )
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/admin/posts?updated"));

        ArgumentCaptor<Long> idCaptor = ArgumentCaptor.forClass(Long.class);
        ArgumentCaptor<PostForm> formCaptor = ArgumentCaptor.forClass(PostForm.class);
        ArgumentCaptor<MultipartFile> fileCaptor = ArgumentCaptor.forClass(MultipartFile.class);

        verify(postService)
                .update(idCaptor.capture(), formCaptor.capture(), fileCaptor.capture());

        assertThat(fileCaptor.getValue()).isNotNull();
        assertThat(fileCaptor.getValue().getOriginalFilename()).isEqualTo("test.jpg");
    }


    /* =====================================================
       7. 権限なしユーザーは403
       ===================================================== */
    @Test
    @WithMockUser(roles = {"USER"})
    void 権限なしユーザーは403() throws Exception {
        mockMvc.perform(get("/admin/posts/edit/1"))
                .andExpect(status().isForbidden());
    }


    /* =====================================================
       8. 未ログイン → admin/login へリダイレクト
       ===================================================== */
    @Test
    void 未ログインならログインへリダイレクト() throws Exception {
        mockMvc.perform(get("/admin/posts/edit/1"))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrlPattern("**/admin/login"));
    }
}