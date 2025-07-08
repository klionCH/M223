package com.example.demo.entity;

import java.time.LocalDateTime;

public class Follow {
    private Long id;
    private Long followerId;
    private Long followingId;
    private LocalDateTime createdAt;

    // Constructors
    public Follow() {}

    public Follow(Long id, Long followerId, Long followingId) {
        this.id = id;
        this.followerId = followerId;
        this.followingId = followingId;
        this.createdAt = LocalDateTime.now();
    }

    // Getters and Setters
    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public Long getFollowerId() { return followerId; }
    public void setFollowerId(Long followerId) { this.followerId = followerId; }

    public Long getFollowingId() { return followingId; }
    public void setFollowingId(Long followingId) { this.followingId = followingId; }

    public LocalDateTime getCreatedAt() { return createdAt; }
    public void setCreatedAt(LocalDateTime createdAt) { this.createdAt = createdAt; }
}
