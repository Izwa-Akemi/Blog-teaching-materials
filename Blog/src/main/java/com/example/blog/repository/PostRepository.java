package com.example.blog.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import com.example.blog.entity.Category;
import com.example.blog.entity.Post;
import com.example.blog.entity.User;

public interface PostRepository extends JpaRepository<Post, Long> {

    List<Post> findByAuthor(User author);

    List<Post> findByCategory(Category category);

    List<Post> findByIsPublished(boolean isPublished);

    List<Post> findByTitleContainingIgnoreCase(String keyword);
    // ★ 追加する：ユーザーの投稿数を返すメソッド
    long countByAuthor(User author);
    long countByCategory(Category category);
    
    
    long countByIsPublished(boolean isPublished);

    List<Post> findTop3ByOrderByCreatedAtDesc();
    
    // ★ PostgreSQL 対応版（月別投稿数）
    @Query("""
        SELECT FUNCTION('to_char', p.createdAt, 'YYYY-MM') AS ym,
               COUNT(p)
        FROM Post p
        GROUP BY ym
        ORDER BY ym
    """)
    List<Object[]> countPostsGroupByMonth();
}