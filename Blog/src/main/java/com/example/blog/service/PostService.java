package com.example.blog.service;

import com.example.blog.dto.PostDto;

import com.example.blog.entity.Post;
import com.example.blog.entity.User;
import com.example.blog.form.PostForm;
import com.example.blog.entity.Category;
import com.example.blog.repository.PostRepository;
import com.example.blog.repository.UserRepository;
import com.example.blog.repository.CategoryRepository;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.stream.Collectors;


@Service
@RequiredArgsConstructor
public class PostService {

    private final PostRepository postRepository;
    private final UserRepository userRepository;
    private final CategoryRepository categoryRepository;

    /* 全記事一覧（管理者向け） */
    public List<PostDto> findAll() {
        return postRepository.findAll().stream()
                .map(this::toDto)
                .collect(Collectors.toList());
    }

    /* 記事取得（ID） → PostDto に修正！ */
    public PostDto findById(Long id) {
        Post post = getPostOrThrow(id);
        return toDto(post);
    }

    /* 記事作成（画像付き） */
    public void create(PostForm form, Long userId, MultipartFile imageFile) {
        User author = userRepository.findById(userId)
                .orElseThrow(() -> new RuntimeException("ユーザーが見つかりません"));

        Category category = categoryRepository.findById(form.getCategoryId())
                .orElseThrow(() -> new RuntimeException("カテゴリーが見つかりません"));

        Post post = Post.builder()
                .title(form.getTitle())
                .content(form.getContent())
                .author(author)
                .category(category)
                .isPublished(form.isPublished())
                .viewCount(0L)
                .createdAt(LocalDateTime.now())
                .updatedAt(LocalDateTime.now())
                .build();

        // 画像があれば保存
        if (imageFile != null && !imageFile.isEmpty()) {
            String imagePath = saveImage(imageFile);
            post.setImagePath(imagePath);
        }

        postRepository.save(post);
    }

    /* 記事更新処理（画像再アップロード対応） */
    public void update(Long id, PostForm form, MultipartFile imageFile) {

        Post post = postRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("記事が見つかりません"));

        Category category = categoryRepository.findById(form.getCategoryId())
                .orElseThrow(() -> new RuntimeException("カテゴリーが見つかりません"));

        post.setTitle(form.getTitle());
        post.setContent(form.getContent());
        post.setCategory(category);
        post.setPublished(form.isPublished());
        post.setUpdatedAt(LocalDateTime.now());

        // 新しい画像があれば上書き
        if (imageFile != null && !imageFile.isEmpty()) {
            String imagePath = saveImage(imageFile);
            post.setImagePath(imagePath);
        }

        postRepository.save(post);
    }

    /* 記事削除 */
    public void delete(Long id) {
        postRepository.deleteById(id);
    }

    /* 画像保存メソッド（完全動作版） */
    private String saveImage(MultipartFile imageFile) {

        try {
            // プロジェクトルート
            String projectRoot = System.getProperty("user.dir");

            // uploads/posts の絶対パス
            String uploadDir = projectRoot + File.separator + "uploads" + File.separator + "posts";

            Path uploadPath = Paths.get(uploadDir);

            // ディレクトリ作成
            if (!Files.exists(uploadPath)) {
                Files.createDirectories(uploadPath);
            }

            // 拡張子
            String originalName = imageFile.getOriginalFilename();
            String ext = StringUtils.getFilenameExtension(originalName);

            // ファイル名
            String filename = System.currentTimeMillis() + "_" + UUID.randomUUID();
            if (ext != null && !ext.isBlank()) {
                filename += "." + ext;
            }

            // 保存先パス
            Path filePath = uploadPath.resolve(filename);

            // 保存
            imageFile.transferTo(filePath.toFile());

            // Web 用パス
            return "/uploads/posts/" + filename;

        } catch (IOException e) {
            throw new RuntimeException("画像保存に失敗しました", e);
        }
    }

    /* Post 取得ヘルパー */
    private Post getPostOrThrow(Long id) {
        return postRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("記事が見つかりません: " + id));
    }

    /* Post → PostDto 変換 */
    private PostDto toDto(Post post) {
        return PostDto.builder()
                .id(post.getId())
                .title(post.getTitle())
                .content(post.getContent())
                .authorName(post.getAuthor().getDisplayName())
                .authorId(post.getAuthor().getId())
                .categoryName(post.getCategory().getName())
                .categoryId(post.getCategory().getId())
                .imagePath(post.getImagePath())  // ← 追加（重要）
                .published(post.isPublished())
                .viewCount(post.getViewCount())
                .createdAt(post.getCreatedAt())
                .updatedAt(post.getUpdatedAt())
                .build();
    }
    
    /* 記事総数 */
    public long countPosts() {
        return postRepository.count();
    }

    /* 公開記事数 */
    public long countPublished() {
        return postRepository.countByIsPublished(true);
    }

    /* 下書き記事数 */
    public long countDraft() {
        return postRepository.countByIsPublished(false);
    }

    /* 最近の3件の記事 */
    public List<PostDto> findRecentPosts(int limit) {
        return postRepository.findTop3ByOrderByCreatedAtDesc()
                .stream().map(this::toDto)
                .collect(Collectors.toList());
    }
    public List<Map<String, Object>> getMonthlyPostStats() {

        List<Object[]> results = postRepository.countPostsGroupByMonth();

        return results.stream()
                .map(row -> {
                    Map<String, Object> map = new HashMap<>();
                    map.put("month", (String) row[0]);
                    map.put("count", ((Number) row[1]).intValue());
                    return map;
                })
                .toList();
    }
}