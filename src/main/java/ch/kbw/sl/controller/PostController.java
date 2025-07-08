package ch.kbw.sl.controller;

import ch.kbw.sl.entity.Post;
import ch.kbw.sl.entity.User;
import ch.kbw.sl.service.PostService;
import ch.kbw.sl.service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/posts")
@RequiredArgsConstructor
@CrossOrigin(origins = "*")
public class PostController {

    private final PostService postService;
    private final UserService userService;

    @GetMapping
    public ResponseEntity<Page<Post>> getAllPosts(
            Pageable pageable,
            @RequestParam(required = false) Post.ContentType contentType) {

        Page<Post> posts = contentType != null
                ? postService.findByContentType(contentType, pageable)
                : postService.findAll(pageable);

        return ResponseEntity.ok(posts);
    }

    @GetMapping("/{id}")
    public ResponseEntity<Post> getPostById(@PathVariable Long id) {
        return postService.findById(id)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    @PostMapping
    public ResponseEntity<Post> createPost(@RequestBody Post post) {
        // Simulate current user (in real app, get from JWT token)
        User currentUser = userService.findById(1L).orElse(null);
        if (currentUser == null) {
            return ResponseEntity.badRequest().build();
        }

        post.setUser(currentUser);
        Post savedPost = postService.createPost(post);
        return ResponseEntity.ok(savedPost);
    }

    @PutMapping("/{id}")
    public ResponseEntity<Post> updatePost(@PathVariable Long id, @RequestBody Post post) {
        try {
            Post updatedPost = postService.updatePost(id, post);
            return ResponseEntity.ok(updatedPost);
        } catch (IllegalArgumentException e) {
            return ResponseEntity.notFound().build();
        }
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deletePost(@PathVariable Long id) {
        try {
            postService.deletePost(id);
            return ResponseEntity.noContent().build();
        } catch (IllegalArgumentException e) {
            return ResponseEntity.notFound().build();
        }
    }

    @GetMapping("/user/{userId}")
    public ResponseEntity<Page<Post>> getPostsByUser(@PathVariable Long userId, Pageable pageable) {
        User user = userService.findById(userId).orElse(null);
        if (user == null) {
            return ResponseEntity.notFound().build();
        }

        Page<Post> posts = postService.findByUser(user, pageable);
        return ResponseEntity.ok(posts);
    }

    @GetMapping("/feed")
    public ResponseEntity<Page<Post>> getFeed(Pageable pageable) {
        // Simulate current user
        User currentUser = userService.findById(1L).orElse(null);
        if (currentUser == null) {
            return ResponseEntity.badRequest().build();
        }

        Page<Post> feed = postService.getFeedForUser(currentUser, pageable);
        return ResponseEntity.ok(feed);
    }
}
