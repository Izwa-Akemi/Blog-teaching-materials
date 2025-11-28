package com.example.blog.controller.admin;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import com.example.blog.form.UserForm;
import com.example.blog.service.UserService;

import lombok.RequiredArgsConstructor;

@Controller
@RequestMapping("/admin/users")
@RequiredArgsConstructor
public class AdminUserEditController {

    private final UserService userService;

    /* 編集画面 */
    @GetMapping("/edit/{id}")
    public String editForm(@PathVariable Long id, Model model) {
        model.addAttribute("userForm", userService.getUserFormById(id));
        return "admin/users/edit";
    }

    /* 更新処理 */
    @PostMapping("/edit/{id}")
    public String update(@PathVariable Long id,
                         @ModelAttribute("userForm") UserForm form,
                         RedirectAttributes redirectAttributes) {

        userService.updateUser(id, form);
        redirectAttributes.addFlashAttribute("success", "管理者情報を更新しました");

        return "redirect:/admin/users/list";
    }
}
