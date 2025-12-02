package com.example.blog.unit.controller.admin;

import static org.hamcrest.CoreMatchers.any;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.model;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.view;

import java.util.Collections;
import java.util.List;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.web.servlet.MockMvc;

import com.example.blog.dto.CategoryDto;
import com.example.blog.dto.PostDto;
import com.example.blog.entity.Category;
import com.example.blog.entity.Post;
import com.example.blog.service.CategoryService;
import com.example.blog.service.PostService;

@SpringBootTest
@AutoConfigureMockMvc
@TestPropertySource(properties = {
        "spring.main.allow-bean-definition-overriding=true"
})
class AdminPostListControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private PostService postService;
    @MockBean
    private CategoryService categoryService;

    /* =====================================================
       1. 検索条件なし → findAll() が呼ばれる
       ===================================================== */
    @Test
    @WithMockUser(username = "admin@test.com", roles = {"ADMIN"})
    void 条件なし_全件取得が呼ばれる() throws Exception {

        List<PostDto> dummyPosts = Collections.emptyList();
        List<CategoryDto> dummyCategories = Collections.emptyList();


        when(postService.findAll()).thenReturn(dummyPosts);
        when(categoryService.findAll()).thenReturn(dummyCategories);

        mockMvc.perform(get("/admin/posts"))
                .andExpect(status().isOk())
                .andExpect(view().name("admin/posts/index"))
                .andExpect(model().attribute("posts", dummyPosts))
                .andExpect(model().attribute("categories", dummyCategories))
                .andExpect(model().attribute("categoryId", (Long) null))
                .andExpect(model().attribute("keyword", (String) null));

        verify(postService, times(1)).findAll();
        verify(postService, never()).searchForAdmin(any(), any());
    }

    /* =====================================================
       2. categoryId がある場合 → searchForAdmin() が呼ばれる
       ===================================================== */
    @Test
    @WithMockUser(username = "admin@test.com", roles = {"ADMIN"})
    void categoryIdあり_searchが呼ばれる() throws Exception {

        List<PostDto> dummyPosts = Collections.emptyList();
        List<CategoryDto> dummyCategories = Collections.emptyList();


        when(postService.searchForAdmin(1L, null)).thenReturn(dummyPosts);
        when(categoryService.findAll()).thenReturn(dummyCategories);

        mockMvc.perform(get("/admin/posts")
                .param("category", "1"))
                .andExpect(status().isOk())
                .andExpect(view().name("admin/posts/index"))
                .andExpect(model().attribute("posts", dummyPosts))
                .andExpect(model().attribute("categories", dummyCategories))
                .andExpect(model().attribute("categoryId", 1L))
                .andExpect(model().attribute("keyword", (String) null));

        verify(postService, times(1)).searchForAdmin(1L, null);
        verify(postService, never()).findAll();
    }

    /* =====================================================
       3. keyword がある場合 → searchForAdmin() が呼ばれる
       ===================================================== */
    @Test
    @WithMockUser(username = "admin@test.com", roles = {"ADMIN"})
    void keywordあり_searchが呼ばれる() throws Exception {

        List<PostDto> dummyPosts = Collections.emptyList();
        List<CategoryDto> dummyCategories = Collections.emptyList();

        when(postService.searchForAdmin(null, "test")).thenReturn(dummyPosts);
        when(categoryService.findAll()).thenReturn(dummyCategories);

        mockMvc.perform(get("/admin/posts")
                .param("keyword", "test"))
                .andExpect(status().isOk())
                .andExpect(view().name("admin/posts/index"))
                .andExpect(model().attribute("posts", dummyPosts))
                .andExpect(model().attribute("categories", dummyCategories))
                .andExpect(model().attribute("categoryId", (Long) null))
                .andExpect(model().attribute("keyword", "test"));

        verify(postService, times(1)).searchForAdmin(null, "test");
        verify(postService, never()).findAll();
    }
}