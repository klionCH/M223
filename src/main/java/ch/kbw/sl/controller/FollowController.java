package ch.kbw.sl.controller;

import ch.kbw.sl.entity.Follow;
import ch.kbw.sl.entity.User;
import ch.kbw.sl.service.FollowService;
import ch.kbw.sl.service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping("/api/users")
@RequiredArgsConstructor
@CrossOrigin(origins = "*")
public class FollowController {

    private final FollowService followService;
    private final UserService userService;

    @PostMapping("/{userId}/follow")
    public ResponseEntity<Map<String, Object>> followUser(@PathVariable Long userId) {
        User currentUser = userService.findById(1L).orElse(null);
        User targetUser = userService.findById(userId).orElse(null);

        if (currentUser == null || targetUser == null) {
            return ResponseEntity.badRequest().build();
        }

        try {
            Follow follow = followService.followUser(currentUser, targetUser);

            Map<String, Object> response = new HashMap<>();
            response.put("success", true);
            response.put("message", "Successfully followed user");
            response.put("followId", follow.getId());

            return ResponseEntity.ok(response);
        } catch (IllegalArgumentException e) {
            Map<String, Object> response = new HashMap<>();
            response.put("success", false);
            response.put("message", e.getMessage());

            return ResponseEntity.badRequest().body(response);
        }
    }

    @DeleteMapping("/{userId}/unfollow")
    public ResponseEntity<Map<String, String>> unfollowUser(@PathVariable Long userId) {
        User currentUser = userService.findById(1L).orElse(null);
        User targetUser = userService.findById(userId).orElse(null);

        if (currentUser == null || targetUser == null) {
            return ResponseEntity.badRequest().build();
        }

        followService.unfollowUser(currentUser, targetUser);

        Map<String, String> response = new HashMap<>();
        response.put("message", "Successfully unfollowed user");

        return ResponseEntity.ok(response);
    }

    @GetMapping("/{userId}/followers")
    public ResponseEntity<Page<Follow>> getFollowers(@PathVariable Long userId, Pageable pageable) {
        User user = userService.findById(userId).orElse(null);
        if (user == null) {
            return ResponseEntity.notFound().build();
        }

        Page<Follow> followers = followService.getFollowers(user, pageable);
        return ResponseEntity.ok(followers);
    }

    @GetMapping("/{userId}/following")
    public ResponseEntity<Page<Follow>> getFollowing(@PathVariable Long userId, Pageable pageable) {
        User user = userService.findById(userId).orElse(null);
        if (user == null) {
            return ResponseEntity.notFound().build();
        }

        Page<Follow> following = followService.getFollowing(user, pageable);
        return ResponseEntity.ok(following);
    }

    @GetMapping("/{userId}/follow-status")
    public ResponseEntity<Map<String, Object>> getFollowStatus(@PathVariable Long userId) {
        User currentUser = userService.findById(1L).orElse(null);
        User targetUser = userService.findById(userId).orElse(null);

        if (currentUser == null || targetUser == null) {
            return ResponseEntity.badRequest().build();
        }

        boolean isFollowing = followService.isFollowing(currentUser, targetUser);
        boolean isFollowedBy = followService.isFollowing(targetUser, currentUser);
        long followerCount = followService.getFollowerCount(targetUser);
        long followingCount = followService.getFollowingCount(targetUser);

        Map<String, Object> status = new HashMap<>();
        status.put("isFollowing", isFollowing);
        status.put("isFollowedBy", isFollowedBy);
        status.put("followerCount", followerCount);
        status.put("followingCount", followingCount);

        return ResponseEntity.ok(status);
    }
}