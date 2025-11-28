package com.example.blog.controller.admin;

import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.multipart.MultipartFile;

import com.example.blog.config.CustomUserDetails;
import com.example.blog.dto.PostDto;
import com.example.blog.form.PostForm;
import com.example.blog.service.CategoryService;
import com.example.blog.service.PostService;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;

@Controller
@RequestMapping("/admin/posts")
@RequiredArgsConstructor
public class AdminPostEditController {
	private final PostService postService;
	  private final CategoryService categoryService;
	
	/* 編集画面表示 */
	  @GetMapping("/edit/{id}")
	  public String editForm(@PathVariable Long id, Model model) {
		  

	      PostDto post = postService.findById(id);  // DTOで取得

	      PostForm form = new PostForm();
	      form.setId(post.getId());
	      form.setTitle(post.getTitle());
	      form.setContent(post.getContent());
	      form.setCategoryId(post.getCategoryId());
	      form.setPublished(post.isPublished());
	      model.addAttribute("postForm", form);
	      model.addAttribute("categories", categoryService.findAll());
	      model.addAttribute("postImage", post.getImagePath());
	      model.addAttribute("authorName", post.getAuthorName());
	      model.addAttribute("createdAt", post.getCreatedAt());

	      return "admin/posts/edit";
	  }

	/* 編集処理 */
	@PostMapping("/edit/{id}")
	public String update(@PathVariable Long id,
	                     @Valid @ModelAttribute("postForm") PostForm form,
	                     BindingResult result,
	                     @RequestParam(name = "imageFile", required = false) MultipartFile imageFile,
	                     Model model) {

	    if (result.hasErrors()) {
	        model.addAttribute("categories", categoryService.findAll());
	        return "admin/posts/edit";
	    }

	    postService.update(id, form, imageFile);

	    return "redirect:/admin/posts?updated";
	}
}
