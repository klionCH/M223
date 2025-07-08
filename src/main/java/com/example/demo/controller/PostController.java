package com.example.demo.controller;

import com.example.demo.entity.Post;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("/posts")
@CrossOrigin(origins = "*")
public class PostController {

    private static List<Post> posts = new ArrayList<>();

    static {
        // Static sample data
        Post post1 = new Post(1L, 1L, "My Latest Photography Project",
                "Check out this amazing sunset I captured yesterday!",
                Post.ContentType.IMAGE);
        post1.setMediaUrl("https://example.com/images/sunset.jpg");

        Post post2 = new Post(2L, 2L, "UI Design Tips",
                "Here are 5 essential tips for better UI design...",
                Post.ContentType.TEXT);

        Post post3 = new Post(3L, 1L, "Behind the Scenes",
                "Quick video showing my creative process",
                Post.ContentType.VIDEO);
        post3.setMediaUrl("https://example.com/videos/process.mp4");

        Post post4 = new Post(4L, 3L, "Daily Inspiration",
                "30-second motivation for creatives",
                Post.ContentType.REEL);
        post4.setMediaUrl("https://example.com/reels/inspiration.mp4");

        posts.add(post1);
        posts.add(post2);
        posts.add(post3);
        posts.add(post4);
    }

    @GetMapping
    public ResponseEntity<List<Post>> getAllPosts(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int limit,
            @RequestParam(required = false) Post.ContentType contentType) {

        List<Post> filteredPosts = posts.stream()
                .filter(post -> contentType == null || post.getContentType() == contentType)
                .skip(page * limit)
                .limit(limit)
                .toList();

        return ResponseEntity.ok(filteredPosts);
    }

    @PostMapping
    public ResponseEntity<Post> createPost(@RequestBody Post post) {
        post.setId((long) (posts.size() + 1));
        post.setCreatedAt(LocalDateTime.now());
        post.setUpdatedAt(LocalDateTime.now());
        post.setActive(true);

        posts.add(post);
        return ResponseEntity.ok(post);
    }

    @GetMapping("/{id}")
    public ResponseEntity<Post> getPostById(@PathVariable Long id) {
        Optional<Post> post = posts.stream()
                .filter(p -> p.getId().equals(id))
                .findFirst();

        if (post.isPresent()) {
            return ResponseEntity.ok(post.get());
        } else {
            return ResponseEntity.notFound().build();
        }
    }

    @PutMapping("/{id}")
    public ResponseEntity<Post> updatePost(@PathVariable Long id, @RequestBody Post updatedPost) {
        Optional<Post> postOpt = posts.stream()
                .filter(p -> p.getId().equals(id))
                .findFirst();

        if (postOpt.isPresent()) {
            Post post = postOpt.get();
            post.setTitle(updatedPost.getTitle());
            post.setContent(updatedPost.getContent());
            post.setUpdatedAt(LocalDateTime.now());
            return ResponseEntity.ok(post);
        } else {
            return ResponseEntity.notFound().build();
        }
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deletePost(@PathVariable Long id) {
        boolean removed = posts.removeIf(p -> p.getId().equals(id));

        if (removed) {
            return ResponseEntity.noContent().build();
        } else {
            return ResponseEntity.notFound().build();
        }
    }

    @GetMapping("/user/{userId}")
    public ResponseEntity<List<Post>> getPostsByUser(
            @PathVariable Long userId,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int limit) {

        List<Post> userPosts = posts.stream()
                .filter(post -> post.getUserId().equals(userId))
                .skip(page * limit)
                .limit(limit)
                .toList();

        return ResponseEntity.ok(userPosts);
    }

    @GetMapping("/feed")
    public ResponseEntity<List<Post>> getFeed(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int limit) {

        // Simulate personalized feed (for now, just return all posts)
        List<Post> feed = posts.stream()
                .skip(page * limit)
                .limit(limit)
                .toList();

        return ResponseEntity.ok(feed);
    }
}