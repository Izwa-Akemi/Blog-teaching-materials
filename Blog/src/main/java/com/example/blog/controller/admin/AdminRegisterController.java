package com.example.blog.controller.admin;

import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import com.example.blog.config.CustomUserDetails;
import com.example.blog.form.PostForm;
import com.example.blog.form.UserForm;
import com.example.blog.service.CategoryService;
import com.example.blog.service.PostService;
import com.example.blog.service.UserService;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;

@Controller
@RequiredArgsConstructor
@RequestMapping("/admin/users")
public class AdminRegisterController {
	private final UserService userService;
	/* 新規作成フォーム */
	@GetMapping("/create")
	public String showCreateForm(Model model) {
		model.addAttribute("userForm", new UserForm());
		return "admin/users/create";
	}
	/* 新規作成処理 */
	@PostMapping("/create")
	public String createAdmin(@Valid @ModelAttribute UserForm form,
	                          BindingResult result,
	                          RedirectAttributes redirectAttributes) {

	    if (result.hasErrors()) {
	        return "admin/users/create";
	    }

	    userService.createAdmin(form);

	    redirectAttributes.addFlashAttribute("success", "管理者を作成しました");
	    return "redirect:/admin/dashboard";
	}
}
