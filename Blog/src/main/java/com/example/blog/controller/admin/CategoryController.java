package com.example.blog.controller.admin;

import java.util.List;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import com.example.blog.dto.CategoryDto;
import com.example.blog.form.CategoryForm;
import com.example.blog.service.CategoryService;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;

@Controller
@RequestMapping("/admin/categories")
@RequiredArgsConstructor
public class CategoryController {

    private final CategoryService categoryService;

    /** カテゴリー一覧 */
    @GetMapping
    public String index(Model model) {
        List<CategoryDto> categories = categoryService.findAll();
        model.addAttribute("categories", categories);
        return "admin/categories/index";
    }

    /** カテゴリー作成フォーム */
    @GetMapping("/create")
    public String createForm(Model model) {
        model.addAttribute("categoryForm", new CategoryForm());
        return "admin/categories/create";
    }

    /** カテゴリー作成実行 */
    @PostMapping("/create")
    public String create(@Valid @ModelAttribute("categoryForm") CategoryForm form,
                         BindingResult result,
                         RedirectAttributes redirectAttributes,
                         Model model) {

        if (result.hasErrors()) {
            return "admin/categories/create";
        }

        try {
            categoryService.create(form);
            redirectAttributes.addFlashAttribute("success", "カテゴリーを作成しました");
        } catch (Exception e) {
            model.addAttribute("errorMessage", e.getMessage());
            return "admin/categories/create";
        }

        return "redirect:/admin/categories";
    }

    /** 編集フォーム */
    @GetMapping("/edit/{id}")
    public String editForm(@PathVariable Long id, Model model) {

        CategoryForm form = categoryService.findById(id);
        model.addAttribute("categoryForm", form);

        return "admin/categories/edit";
    }

    /** 編集実行 */
    @PostMapping("/edit")
    public String update(@Valid @ModelAttribute("categoryForm") CategoryForm form,
                         BindingResult result,
                         RedirectAttributes redirectAttributes,
                         Model model) {

        if (result.hasErrors()) {
            return "admin/categories/edit";
        }

        try {
            categoryService.update(form);
            redirectAttributes.addFlashAttribute("success", "カテゴリーを更新しました");
        } catch (Exception e) {
            model.addAttribute("errorMessage", e.getMessage());
            return "admin/categories/edit";
        }

        return "redirect:/admin/categories";
    }

    /** 削除（POST + CSRF 付き） */
    @PostMapping("/delete/{id}")
    public String delete(@PathVariable Long id, RedirectAttributes attrs) {

        if (!categoryService.canDelete(id)) {
            attrs.addFlashAttribute("error", "このカテゴリーは記事に使用されているため削除できません。");
            return "redirect:/admin/categories";
        }

        categoryService.delete(id);
        attrs.addFlashAttribute("success", "削除しました。");

        return "redirect:/admin/categories";
    }
}