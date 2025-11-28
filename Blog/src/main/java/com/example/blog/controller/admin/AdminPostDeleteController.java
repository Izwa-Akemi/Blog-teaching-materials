package com.example.blog.controller.admin;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import com.example.blog.service.PostService;

import lombok.RequiredArgsConstructor;

@Controller
@RequestMapping("/admin/posts")
@RequiredArgsConstructor
public class AdminPostDeleteController {
	private final PostService postService;
	@PostMapping("/delete/{id}")
	public String delete(@PathVariable Long id, RedirectAttributes redirectAttributes) {

	    // 削除
	    postService.delete(id);

	    redirectAttributes.addFlashAttribute("success", "記事を削除しました。");
	    return "redirect:/admin/posts";
	}
}
