package com.example.blog.service;

import com.example.blog.dto.CommentDto;
import com.example.blog.entity.Comment;
import com.example.blog.entity.Post;
import com.example.blog.entity.User;
import com.example.blog.form.CommentForm;
import com.example.blog.repository.CommentRepository;
import com.example.blog.repository.PostRepository;
import com.example.blog.repository.UserRepository;

import lombok.RequiredArgsConstructor;

import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class CommentService {

    private final CommentRepository commentRepository;
    private final UserRepository userRepository;
    private final PostRepository postRepository;

    /* コメント一覧（管理用） */
    public List<CommentDto> findAll() {
        return commentRepository.findAll().stream()
                .map(this::toDto)
                .collect(Collectors.toList());
    }

    /* コメント投稿 */
    public void create(CommentForm form, Long userId) {
        User author = userRepository.findById(userId)
                .orElseThrow(() -> new RuntimeException("ユーザーが見つかりません"));

        Post post = postRepository.findById(form.getPostId())
                .orElseThrow(() -> new RuntimeException("記事が見つかりません"));

        Comment comment = Comment.builder()
                .author(author)
                .post(post)
                .content(form.getContent())
                .approved(true)
                .deleted(false)
                .build();

        commentRepository.save(comment);
    }

    /* 削除（管理者） */
    public void delete(Long id) {
        Comment comment = commentRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("コメントが見つかりません"));

        comment.setDeleted(true);
        commentRepository.save(comment);
    }

    private CommentDto toDto(Comment c) {
        return CommentDto.builder()
                .id(c.getId())
                .postId(c.getPost().getId())
                .postTitle(c.getPost().getTitle())
                .userId(c.getAuthor().getId())
                .authorName(c.getAuthor().getDisplayName())
                .content(c.getContent())
                .approved(c.isApproved())
                .deleted(c.isDeleted())
                .createdAt(c.getCreatedAt())
                .build();
    }
    
    public long countComments() {
        return commentRepository.count();
    }

    public List<CommentDto> findRecentComments(int limit) {
        return commentRepository.findRecentComments(PageRequest.of(0, limit))
                .stream()
                .map(this::toDto)
                .toList();
    }

    public List<CommentDto> findByPostId(Long postId, int page, int size) {
        return commentRepository.findByPostIdAndDeletedFalse(postId,
                PageRequest.of(page, size))
                .stream()
                .map(this::toDto)
                .toList();
    }

    public long countByPostId(Long postId) {
        return commentRepository.countByPostIdAndDeletedFalse(postId);
    }
    
    public void deleteByUser(Long commentId, Long userId) {
        Comment comment = commentRepository.findById(commentId)
                .orElseThrow(() -> new RuntimeException("コメントが見つかりません"));

        if (!comment.getAuthor().getId().equals(userId)) {
            throw new RuntimeException("自分のコメント以外は削除できません");
        }

        comment.setDeleted(true);
        commentRepository.save(comment);
    }
    public void updateByUser(Long commentId, Long userId, String content) {

        Comment comment = commentRepository.findById(commentId)
                .orElseThrow(() -> new RuntimeException("コメントが見つかりません"));

        if (!comment.getAuthor().getId().equals(userId)) {
            throw new RuntimeException("自分のコメント以外は編集できません");
        }

        comment.setContent(content);
        commentRepository.save(comment);
    }

    public List<CommentDto> findAllForAdmin() {
        return commentRepository.findAllForAdmin()
                .stream()
                .map(this::toDto)
                .toList();
    }

    // 管理者が削除（論理削除）
    public void adminDelete(Long id) {
        Comment comment = commentRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("コメントが見つかりません"));

        comment.setDeleted(true);
        commentRepository.save(comment);
    }

    // 管理者が承認切り替え
    public void toggleApproval(Long id) {
        Comment comment = commentRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("コメントが見つかりません"));

        comment.setApproved(!comment.isApproved());
        commentRepository.save(comment);
    }

    


}
