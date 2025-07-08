package com.example.demo.controller;

import com.example.demo.entity.Follow;
import com.example.demo.entity.User;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/users")
@CrossOrigin(origins = "*")
public class FollowController {

    private static List<Follow> follows = new ArrayList<>();

    static {
        // Static sample follow relationships
        follows.add(new Follow(1L, 1L, 2L)); // John follows Jane
        follows.add(new Follow(2L, 1L, 3L)); // John follows Mike
        follows.add(new Follow(3L, 2L, 1L)); // Jane follows John
        follows.add(new Follow(4L, 3L, 1L)); // Mike follows John
        follows.add(new Follow(5L, 3L, 2L)); // Mike follows Jane
    }

    @PostMapping("/{userId}/follow")
    public ResponseEntity<Map<String, Object>> followUser(@PathVariable Long userId) {
        // Simulate current user (in real app, get from JWT token)
        Long currentUserId = 1L;

        // Check if already following
        boolean alreadyFollowing = follows.stream()
                .anyMatch(f -> f.getFollowerId().equals(currentUserId) &&
                        f.getFollowingId().equals(userId));

        if (alreadyFollowing) {
            Map<String, Object> response = new HashMap<>();
            response.put("success", false);
            response.put("message", "Already following this user");
            return ResponseEntity.badRequest().body(response);
        }

        Follow follow = new Follow((long) (follows.size() + 1), currentUserId, userId);
        follows.add(follow);

        Map<String, Object> response = new HashMap<>();
        response.put("success", true);
        response.put("message", "Successfully followed user");
        response.put("followId", follow.getId());

        return ResponseEntity.ok(response);
    }

    @DeleteMapping("/{userId}/unfollow")
    public ResponseEntity<Map<String, String>> unfollowUser(@PathVariable Long userId) {
        // Simulate current user
        Long currentUserId = 1L;

        boolean removed = follows.removeIf(f ->
                f.getFollowerId().equals(currentUserId) &&
                        f.getFollowingId().equals(userId));

        Map<String, String> response = new HashMap<>();
        if (removed) {
            response.put("message", "Successfully unfollowed user");
            return ResponseEntity.ok(response);
        } else {
            response.put("message", "Follow relationship not found");
            return ResponseEntity.notFound().build();
        }
    }

    @GetMapping("/{userId}/followers")
    public ResponseEntity<List<Map<String, Object>>> getFollowers(
            @PathVariable Long userId,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int limit) {

        List<Map<String, Object>> followers = follows.stream()
                .filter(f -> f.getFollowingId().equals(userId))
                .skip(page * limit)
                .limit(limit)
                .map(f -> {
                    Map<String, Object> follower = new HashMap<>();
                    follower.put("followerId", f.getFollowerId());
                    follower.put("followedAt", f.getCreatedAt());
                    // In real app, would fetch user details
                    follower.put("username", "user_" + f.getFollowerId());
                    return follower;
                })
                .toList();

        return ResponseEntity.ok(followers);
    }

    @GetMapping("/{userId}/following")
    public ResponseEntity<List<Map<String, Object>>> getFollowing(
            @PathVariable Long userId,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int limit) {

        List<Map<String, Object>> following = follows.stream()
                .filter(f -> f.getFollowerId().equals(userId))
                .skip(page * limit)
                .limit(limit)
                .map(f -> {
                    Map<String, Object> followedUser = new HashMap<>();
                    followedUser.put("followingId", f.getFollowingId());
                    followedUser.put("followedAt", f.getCreatedAt());
                    // In real app, would fetch user details
                    followedUser.put("username", "user_" + f.getFollowingId());
                    return followedUser;
                })
                .toList();

        return ResponseEntity.ok(following);
    }

    @GetMapping("/{userId}/follow-status")
    public ResponseEntity<Map<String, Object>> getFollowStatus(@PathVariable Long userId) {
        // Simulate current user
        Long currentUserId = 1L;

        boolean isFollowing = follows.stream()
                .anyMatch(f -> f.getFollowerId().equals(currentUserId) &&
                        f.getFollowingId().equals(userId));

        boolean isFollowedBy = follows.stream()
                .anyMatch(f -> f.getFollowerId().equals(userId) &&
                        f.getFollowingId().equals(currentUserId));

        long followerCount = follows.stream()
                .filter(f -> f.getFollowingId().equals(userId))
                .count();

        long followingCount = follows.stream()
                .filter(f -> f.getFollowerId().equals(userId))
                .count();

        Map<String, Object> status = new HashMap<>();
        status.put("isFollowing", isFollowing);
        status.put("isFollowedBy", isFollowedBy);
        status.put("followerCount", followerCount);
        status.put("followingCount", followingCount);

        return ResponseEntity.ok(status);
    }
}