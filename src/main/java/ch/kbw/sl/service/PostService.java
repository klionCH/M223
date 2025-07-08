package ch.kbw.sl.service;

import ch.kbw.sl.entity.Post;
import ch.kbw.sl.entity.User;
import ch.kbw.sl.repository.PostRepository;
import ch.kbw.sl.repository.FollowRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
@Transactional
public class PostService {

    private final PostRepository postRepository;
    private final FollowRepository followRepository;

    public Post createPost(Post post) {
        return postRepository.save(post);
    }

    @Transactional(readOnly = true)
    public Optional<Post> findById(Long id) {
        return postRepository.findById(id);
    }

    @Transactional(readOnly = true)
    public Page<Post> findAll(Pageable pageable) {
        return postRepository.findByIsActiveTrueOrderByCreatedAtDesc(pageable);
    }

    @Transactional(readOnly = true)
    public Page<Post> findByUser(User user, Pageable pageable) {
        return postRepository.findByUserOrderByCreatedAtDesc(user, pageable);
    }

    @Transactional(readOnly = true)
    public Page<Post> findByContentType(Post.ContentType contentType, Pageable pageable) {
        return postRepository.findByContentTypeOrderByCreatedAtDesc(contentType, pageable);
    }

    @Transactional(readOnly = true)
    public Page<Post> getFeedForUser(User user, Pageable pageable) {
        List<Long> followingIds = followRepository.findFollowingUserIds(user);
        followingIds.add(user.getId()); // Include own posts
        return postRepository.findFeedPosts(followingIds, pageable);
    }

    public Post updatePost(Long id, Post updatedPost) {
        Post post = postRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Post not found"));

        post.setTitle(updatedPost.getTitle());
        post.setContent(updatedPost.getContent());
        post.setMediaUrl(updatedPost.getMediaUrl());

        return postRepository.save(post);
    }

    public void deletePost(Long id) {
        Post post = postRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Post not found"));
        post.setIsActive(false);
        postRepository.save(post);
    }
}