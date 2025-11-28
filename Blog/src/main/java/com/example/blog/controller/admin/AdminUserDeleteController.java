package com.example.blog.controller.admin;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import com.example.blog.service.UserService;

import lombok.RequiredArgsConstructor;

@Controller
@RequestMapping("/admin/users")
@RequiredArgsConstructor
public class AdminUserDeleteController {
	 private final UserService userService;
	 @PostMapping("/delete/{id}")
	public String delete(@PathVariable Long id,
	                     RedirectAttributes redirectAttributes) {

	    try {
	        userService.deleteAdminSafely(id);
	        redirectAttributes.addFlashAttribute("success", "管理者を削除しました。");
	    } catch (IllegalStateException e) {
	        redirectAttributes.addFlashAttribute("error", e.getMessage());
	    }

	    return "redirect:/admin/users/list";
	}
}
