package com.example.blog.service;



import com.example.blog.dto.CategoryDto;

import com.example.blog.entity.Category;
import com.example.blog.form.CategoryForm;
import com.example.blog.repository.CategoryRepository;
import com.example.blog.repository.PostRepository;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class CategoryService {

    private final CategoryRepository categoryRepository;
    private final PostRepository postRepository;

    public List<CategoryDto> findAll() {
        return categoryRepository.findAll().stream()
                .map(cat -> CategoryDto.builder()
                        .id(cat.getId())
                        .name(cat.getName())
                        .slug(cat.getSlug())
                        .postCount(postRepository.countByCategory(cat))
                        .build())
                .collect(Collectors.toList());
    }

    public void create(CategoryForm form) {
        Category cat = Category.builder()
                .name(form.getName())
                .slug(form.getSlug())
                .build();

        categoryRepository.save(cat);
    }

    public CategoryForm findById(Long id) {
        Category cat = getCategoryOrThrow(id);

        CategoryForm form = new CategoryForm();
        form.setId(cat.getId());
        form.setName(cat.getName());
        form.setSlug(cat.getSlug());

        return form;
    }

    public void update(CategoryForm form) {
        Category cat = getCategoryOrThrow(form.getId());

        cat.setName(form.getName());
        cat.setSlug(form.getSlug());

        categoryRepository.save(cat);
    }

    public void delete(Long id) {
        categoryRepository.deleteById(id);
    }

    private Category getCategoryOrThrow(Long id) {
        return categoryRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("カテゴリーが見つかりません: " + id));
    }
    public boolean canDelete(Long id) {
        Category category = getCategoryOrThrow(id);
        return postRepository.countByCategory(category) == 0;
    }
    
    public long countCategories() {
        return categoryRepository.count();
    }

    public List<String> getCategoryNames() {
        return findAll().stream().map(CategoryDto::getName).toList();
    }

    public List<Long> getCategoryPostCounts() {
        return findAll().stream().map(CategoryDto::getPostCount).toList();
    }

}
