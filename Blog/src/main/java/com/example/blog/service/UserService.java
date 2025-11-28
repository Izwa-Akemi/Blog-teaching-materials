package com.example.blog.service;

import com.example.blog.config.CustomUserDetails;
import com.example.blog.dto.UserDto;

import com.example.blog.entity.User;
import com.example.blog.entity.role.Role;
import com.example.blog.form.UserForm;
import com.example.blog.repository.UserRepository;
import com.example.blog.repository.PostRepository;


import lombok.RequiredArgsConstructor;

import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class UserService {

	private final UserRepository userRepository;
	private final PostRepository postRepository;
	private final PasswordEncoder passwordEncoder;

	public long countUsers() {
		return userRepository.count();
	}
	/* ユーザー一覧を取得（管理者用） */
	public List<UserDto> findAll() {
		return userRepository.findAll().stream()
				.map(user -> UserDto.builder()
						.id(user.getId())
						.username(user.getUsername())
						.displayName(user.getDisplayName())
						.role(user.getRole())
						.enabled(user.isEnabled())
						.createdAt(user.getCreatedAt())
						.updatedAt(user.getUpdatedAt())
						.postCount(postRepository.countByAuthor(user))
						.build())
				.collect(Collectors.toList());
	}

	public void createAdmin(UserForm form) {
		User user = User.builder()
				.username(form.getUsername())
				.displayName(form.getDisplayName())
				.email(form.getEmail())
				.password(passwordEncoder.encode(form.getPassword()))
				.role(Role.ROLE_ADMIN)  // ← ここで管理者として固定
				.enabled(true)
				.build();

		userRepository.save(user);
	}

	/* 管理者一覧取得 */
	public List<User> findAllAdmins() {
		return userRepository.findByRole(Role.ROLE_ADMIN);
	}

	/* 削除処理 */
	public void deleteUser(Long id) {
		userRepository.deleteById(id);
	}
	/* 編集画面用フォーム取得 */
	public UserForm getUserFormById(Long id) {
		User user = userRepository.findById(id)
				.orElseThrow(() -> new RuntimeException("ユーザーが見つかりません"));

		UserForm form = new UserForm();
		form.setId(user.getId());
		form.setUsername(user.getUsername());
		form.setDisplayName(user.getDisplayName());
		form.setEmail(user.getEmail());

		return form;
	}

	/* 更新処理 */
	public void updateUser(Long id, UserForm form) {

		User user = userRepository.findById(id)
				.orElseThrow(() -> new RuntimeException("更新対象のユーザーが見つかりません"));

		user.setUsername(form.getUsername());
		user.setDisplayName(form.getDisplayName());
		user.setEmail(form.getEmail());

		// パスワード変更（空欄ならスルー）
		if (form.getPassword() != null && !form.getPassword().isBlank()) {
			user.setPassword(passwordEncoder.encode(form.getPassword()));
		}

		userRepository.save(user);
	}
	/* 現在ログイン中のユーザー取得 */
	public User getCurrentUser() {
		Authentication auth = SecurityContextHolder.getContext().getAuthentication();
		CustomUserDetails userDetails = (CustomUserDetails) auth.getPrincipal();
		return userDetails.getUser();
	}
	public void deleteAdminSafely(Long id) {

		User current = getCurrentUser();

		if (current.getId().equals(id)) {
			throw new IllegalStateException("自分自身のアカウントは削除できません。");
		}

		User target = userRepository.findById(id)
				.orElseThrow(() -> new IllegalStateException("削除対象が見つかりません。"));

		if (!target.getRole().equals(Role.ROLE_ADMIN)) {
			throw new IllegalStateException("管理者以外の削除はできません。");
		}

		long adminCount = userRepository.countByRole(Role.ROLE_ADMIN);
		if (adminCount <= 1) {
			throw new IllegalStateException("最後の管理者は削除できません。");
		}

		userRepository.delete(target);
	}


	public long countAdmins() {
	    return userRepository.countByRole(Role.ROLE_ADMIN);
	}

	public long countGeneralUsers() {
	    return userRepository.count() - countAdmins();
	}

	public List<User> findRecentUsers(int limit) {
	    return userRepository.findTop3ByOrderByCreatedAtDesc();
	}
	/* 一般ユーザー登録（サイトの読者など） */
	public void registerUser(UserForm form) {

	    // メール重複チェック
	    if (userRepository.findByEmail(form.getEmail()).isPresent()) {
	        throw new IllegalStateException("このメールアドレスは既に使用されています");
	    }

	    User user = User.builder()
	            .username(form.getUsername())
	            .displayName(form.getDisplayName())
	            .email(form.getEmail())
	            .password(passwordEncoder.encode(form.getPassword()))
	            .role(Role.ROLE_USER)  // ★ 一般ユーザーとして登録
	            .enabled(true)
	            .build();

	    userRepository.save(user);
	}

}
