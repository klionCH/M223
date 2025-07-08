package ch.kbw.sl.controller;

import ch.kbw.sl.entity.Interaction;
import ch.kbw.sl.entity.Post;
import ch.kbw.sl.entity.User;
import ch.kbw.sl.service.InteractionService;
import ch.kbw.sl.service.PostService;
import ch.kbw.sl.service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/interactions")
@RequiredArgsConstructor
@CrossOrigin(origins = "*")
public class InteractionController {

    private final InteractionService interactionService;
    private final UserService userService;
    private final PostService postService;

    @PostMapping
    public ResponseEntity<Interaction> createInteraction(@RequestBody Interaction interaction) {
        // Simulate current user
        User currentUser = userService.findById(1L).orElse(null);
        if (currentUser == null) {
            return ResponseEntity.badRequest().build();
        }

        interaction.setUser(currentUser);

        try {
            Interaction savedInteraction = interactionService.createInteraction(interaction);
            return ResponseEntity.ok(savedInteraction);
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().build();
        }
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteInteraction(@PathVariable Long id) {
        interactionService.deleteInteraction(id);
        return ResponseEntity.noContent().build();
    }

    @GetMapping("/posts/{postId}")
    public ResponseEntity<Page<Interaction>> getPostInteractions(@PathVariable Long postId, Pageable pageable) {
        Post post = postService.findById(postId).orElse(null);
        if (post == null) {
            return ResponseEntity.notFound().build();
        }

        Page<Interaction> interactions = interactionService.findByPost(post, pageable);
        return ResponseEntity.ok(interactions);
    }

    @GetMapping("/posts/{postId}/likes")
    public ResponseEntity<List<Interaction>> getPostLikes(@PathVariable Long postId) {
        Post post = postService.findById(postId).orElse(null);
        if (post == null) {
            return ResponseEntity.notFound().build();
        }

        List<Interaction> likes = interactionService.findLikesByPost(post);
        return ResponseEntity.ok(likes);
    }

    @GetMapping("/posts/{postId}/comments")
    public ResponseEntity<List<Interaction>> getPostComments(@PathVariable Long postId) {
        Post post = postService.findById(postId).orElse(null);
        if (post == null) {
            return ResponseEntity.notFound().build();
        }

        List<Interaction> comments = interactionService.findCommentsByPost(post);
        return ResponseEntity.ok(comments);
    }

    @PostMapping("/posts/{postId}/like")
    public ResponseEntity<Map<String, Object>> likePost(@PathVariable Long postId) {
        User currentUser = userService.findById(1L).orElse(null);
        Post post = postService.findById(postId).orElse(null);

        if (currentUser == null || post == null) {
            return ResponseEntity.badRequest().build();
        }

        try {
            Interaction like = Interaction.builder()
                    .user(currentUser)
                    .post(post)
                    .interactionType(Interaction.InteractionType.LIKE)
                    .build();

            Interaction savedLike = interactionService.createInteraction(like);

            Map<String, Object> response = new HashMap<>();
            response.put("success", true);
            response.put("message", "Post liked successfully");
            response.put("interactionId", savedLike.getId());

            return ResponseEntity.ok(response);
        } catch (IllegalArgumentException e) {
            Map<String, Object> response = new HashMap<>();
            response.put("success", false);
            response.put("message", e.getMessage());

            return ResponseEntity.badRequest().body(response);
        }
    }

    @DeleteMapping("/posts/{postId}/unlike")
    public ResponseEntity<Map<String, String>> unlikePost(@PathVariable Long postId) {
        User currentUser = userService.findById(1L).orElse(null);
        Post post = postService.findById(postId).orElse(null);

        if (currentUser == null || post == null) {
            return ResponseEntity.badRequest().build();
        }

        interactionService.unlikePost(currentUser, post);

        Map<String, String> response = new HashMap<>();
        response.put("message", "Post unliked successfully");

        return ResponseEntity.ok(response);
    }
}