package com.example.demo.controller;


import com.example.demo.entity.Interaction;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/interactions")
@CrossOrigin(origins = "*")
public class InteractionController {

    private static List<Interaction> interactions = new ArrayList<>();

    static {
        // Static sample data
        Interaction like1 = new Interaction(1L, 2L, 1L, Interaction.InteractionType.LIKE);
        Interaction comment1 = new Interaction(2L, 3L, 1L, Interaction.InteractionType.COMMENT);
        comment1.setCommentText("Amazing photo! Love the colors.");

        Interaction like2 = new Interaction(3L, 1L, 2L, Interaction.InteractionType.LIKE);
        Interaction comment2 = new Interaction(4L, 1L, 2L, Interaction.InteractionType.COMMENT);
        comment2.setCommentText("Great tips, thanks for sharing!");

        interactions.add(like1);
        interactions.add(comment1);
        interactions.add(like2);
        interactions.add(comment2);
    }

    @PostMapping
    public ResponseEntity<Interaction> createInteraction(@RequestBody Interaction interaction) {
        interaction.setId((long) (interactions.size() + 1));
        interaction.setCreatedAt(LocalDateTime.now());

        interactions.add(interaction);
        return ResponseEntity.ok(interaction);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteInteraction(@PathVariable Long id) {
        boolean removed = interactions.removeIf(i -> i.getId().equals(id));

        if (removed) {
            return ResponseEntity.noContent().build();
        } else {
            return ResponseEntity.notFound().build();
        }
    }

    @GetMapping("/posts/{postId}")
    public ResponseEntity<List<Interaction>> getPostInteractions(
            @PathVariable Long postId,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int limit) {

        List<Interaction> postInteractions = interactions.stream()
                .filter(interaction -> interaction.getPostId().equals(postId))
                .skip(page * limit)
                .limit(limit)
                .toList();

        return ResponseEntity.ok(postInteractions);
    }

    @GetMapping("/posts/{postId}/likes")
    public ResponseEntity<List<Interaction>> getPostLikes(@PathVariable Long postId) {
        List<Interaction> likes = interactions.stream()
                .filter(i -> i.getPostId().equals(postId) &&
                        i.getInteractionType() == Interaction.InteractionType.LIKE)
                .toList();

        return ResponseEntity.ok(likes);
    }

    @GetMapping("/posts/{postId}/comments")
    public ResponseEntity<List<Interaction>> getPostComments(
            @PathVariable Long postId,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int limit) {

        List<Interaction> comments = interactions.stream()
                .filter(i -> i.getPostId().equals(postId) &&
                        i.getInteractionType() == Interaction.InteractionType.COMMENT)
                .skip(page * limit)
                .limit(limit)
                .toList();

        return ResponseEntity.ok(comments);
    }

    @PostMapping("/posts/{postId}/like")
    public ResponseEntity<Map<String, Object>> likePost(@PathVariable Long postId) {
        // Simulate current user ID (in real app, get from JWT token)
        Long userId = 1L;

        Interaction like = new Interaction((long) (interactions.size() + 1), userId, postId,
                Interaction.InteractionType.LIKE);
        interactions.add(like);

        Map<String, Object> response = new HashMap<>();
        response.put("success", true);
        response.put("message", "Post liked successfully");
        response.put("interactionId", like.getId());

        return ResponseEntity.ok(response);
    }

    @DeleteMapping("/posts/{postId}/unlike")
    public ResponseEntity<Map<String, String>> unlikePost(@PathVariable Long postId) {
        // Simulate current user ID
        Long userId = 1L;

        boolean removed = interactions.removeIf(i ->
                i.getPostId().equals(postId) &&
                        i.getUserId().equals(userId) &&
                        i.getInteractionType() == Interaction.InteractionType.LIKE);

        Map<String, String> response = new HashMap<>();
        if (removed) {
            response.put("message", "Post unliked successfully");
            return ResponseEntity.ok(response);
        } else {
            response.put("message", "Like not found");
            return ResponseEntity.notFound().build();
        }
    }
}
