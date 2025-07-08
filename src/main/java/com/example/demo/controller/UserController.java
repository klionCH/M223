package com.example.demo.controller;

// UserController.java
import com.example.demo.entity.User;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("/users")
@CrossOrigin(origins = "*")
public class UserController {

    private static List<User> users = new ArrayList<>();

    static {
        // Static sample data
        User user1 = new User(1L, "johndoe", "john@example.com", "John", "Doe");
        user1.setBio("Creative developer and photographer");
        user1.setProfileImage("https://example.com/profiles/john.jpg");

        User user2 = new User(2L, "janesmith", "jane@example.com", "Jane", "Smith");
        user2.setBio("Digital artist and UI/UX designer");
        user2.setProfileImage("https://example.com/profiles/jane.jpg");

        User user3 = new User(3L, "mikechen", "mike@example.com", "Mike", "Chen");
        user3.setBio("Video creator and content strategist");
        user3.setProfileImage("https://example.com/profiles/mike.jpg");

        users.add(user1);
        users.add(user2);
        users.add(user3);
    }

    @GetMapping("/{id}")
    public ResponseEntity<User> getUserById(@PathVariable Long id) {
        Optional<User> user = users.stream()
                .filter(u -> u.getId().equals(id))
                .findFirst();

        if (user.isPresent()) {
            return ResponseEntity.ok(user.get());
        } else {
            return ResponseEntity.notFound().build();
        }
    }

    @GetMapping("/search")
    public ResponseEntity<List<User>> searchUsers(@RequestParam String query) {
        List<User> searchResults = users.stream()
                .filter(u -> u.getUsername().toLowerCase().contains(query.toLowerCase()) ||
                        u.getFullName().toLowerCase().contains(query.toLowerCase()))
                .toList();

        return ResponseEntity.ok(searchResults);
    }

    @PutMapping("/{id}")
    public ResponseEntity<User> updateUser(@PathVariable Long id, @RequestBody User updatedUser) {
        Optional<User> userOpt = users.stream()
                .filter(u -> u.getId().equals(id))
                .findFirst();

        if (userOpt.isPresent()) {
            User user = userOpt.get();
            user.setFirstName(updatedUser.getFirstName());
            user.setLastName(updatedUser.getLastName());
            user.setBio(updatedUser.getBio());
            user.setProfileImage(updatedUser.getProfileImage());
            return ResponseEntity.ok(user);
        } else {
            return ResponseEntity.notFound().build();
        }
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteUser(@PathVariable Long id) {
        boolean removed = users.removeIf(u -> u.getId().equals(id));

        if (removed) {
            return ResponseEntity.noContent().build();
        } else {
            return ResponseEntity.notFound().build();
        }
    }
}