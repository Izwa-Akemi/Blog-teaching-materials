package com.example.blog.repository;


import java.util.List;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import com.example.blog.entity.Comment;
import com.example.blog.entity.Post;
import com.example.blog.entity.User;

public interface CommentRepository extends JpaRepository<Comment, Long> {

    List<Comment> findByPost(Post post);

    List<Comment> findByAuthor(User user);

    List<Comment> findByApproved(boolean approved);

    List<Comment> findByDeleted(boolean deleted);
    
    List<Comment> findTop5ByOrderByCreatedAtDesc();
    long count();
    @Query("SELECT c FROM Comment c WHERE c.deleted = false ORDER BY c.createdAt DESC")
    List<Comment> findRecentComments(Pageable pageable);

    Page<Comment> findByPostIdAndDeletedFalse(Long postId, Pageable pageable);

    long countByPostIdAndDeletedFalse(Long postId);
    
    @Query("SELECT c FROM Comment c ORDER BY c.createdAt DESC")
    List<Comment> findAllForAdmin();



}