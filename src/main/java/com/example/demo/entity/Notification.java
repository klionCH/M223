package com.example.demo.entity;

import java.time.LocalDateTime;

public class Notification {
    private Long id;
    private Long userId;
    private NotificationType type;
    private String message;
    private Long relatedPostId;
    private Long relatedUserId;
    private LocalDateTime createdAt;
    private boolean isRead;

    public enum NotificationType {
        LIKE, COMMENT, FOLLOW, GROUP_INVITE, MENTION, SHARE
    }

    // Constructors
    public Notification() {}

    public Notification(Long id, Long userId, NotificationType type, String message) {
        this.id = id;
        this.userId = userId;
        this.type = type;
        this.message = message;
        this.createdAt = LocalDateTime.now();
        this.isRead = false;
    }

    // Getters and Setters
    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public Long getUserId() { return userId; }
    public void setUserId(Long userId) { this.userId = userId; }

    public NotificationType getType() { return type; }
    public void setType(NotificationType type) { this.type = type; }

    public String getMessage() { return message; }
    public void setMessage(String message) { this.message = message; }

    public Long getRelatedPostId() { return relatedPostId; }
    public void setRelatedPostId(Long relatedPostId) { this.relatedPostId = relatedPostId; }

    public Long getRelatedUserId() { return relatedUserId; }
    public void setRelatedUserId(Long relatedUserId) { this.relatedUserId = relatedUserId; }

    public LocalDateTime getCreatedAt() { return createdAt; }
    public void setCreatedAt(LocalDateTime createdAt) { this.createdAt = createdAt; }

    public boolean isRead() { return isRead; }
    public void setRead(boolean read) { isRead = read; }
}