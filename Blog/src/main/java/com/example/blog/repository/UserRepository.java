package com.example.blog.repository;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

import com.example.blog.entity.User;
import com.example.blog.entity.role.Role;

public interface UserRepository extends JpaRepository<User, Long> {

    Optional<User> findByUsername(String username);

    List<User> findByRole(Role role);

    List<User> findByEnabled(boolean enabled);
    Optional<User> findByEmail(String email);
    // ロールでフィルタしてカウント（最後の管理者削除対策）
    long countByRole(Role role);
    
    List<User> findTop3ByOrderByCreatedAtDesc();

}