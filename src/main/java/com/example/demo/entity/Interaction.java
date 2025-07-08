package com.example.demo.entity;

import java.time.LocalDateTime;

public class Interaction {
    private Long id;
    private Long userId;
    private Long postId;
    private InteractionType interactionType;
    private String commentText;
    private LocalDateTime createdAt;

    public enum InteractionType {
        LIKE, COMMENT, SHARE
    }

    // Constructors
    public Interaction() {}

    public Interaction(Long id, Long userId, Long postId, InteractionType interactionType) {
        this.id = id;
        this.userId = userId;
        this.postId = postId;
        this.interactionType = interactionType;
        this.createdAt = LocalDateTime.now();
    }

    // Getters and Setters
    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public Long getUserId() { return userId; }
    public void setUserId(Long userId) { this.userId = userId; }

    public Long getPostId() { return postId; }
    public void setPostId(Long postId) { this.postId = postId; }

    public InteractionType getInteractionType() { return interactionType; }
    public void setInteractionType(InteractionType interactionType) { this.interactionType = interactionType; }

    public String getCommentText() { return commentText; }
    public void setCommentText(String commentText) { this.commentText = commentText; }

    public LocalDateTime getCreatedAt() { return createdAt; }
    public void setCreatedAt(LocalDateTime createdAt) { this.createdAt = createdAt; }
}
