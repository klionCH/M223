package com.example.demo.entity;

import java.time.LocalDateTime;

public class Group {
    private Long id;
    private String name;
    private String description;
    private Long creatorId;
    private LocalDateTime createdAt;
    private boolean isPrivate;
    private boolean active;

    // Constructors
    public Group() {}

    public Group(Long id, String name, String description, Long creatorId, boolean isPrivate) {
        this.id = id;
        this.name = name;
        this.description = description;
        this.creatorId = creatorId;
        this.isPrivate = isPrivate;
        this.createdAt = LocalDateTime.now();
        this.active = true;
    }

    // Getters and Setters
    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public String getName() { return name; }
    public void setName(String name) { this.name = name; }

    public String getDescription() { return description; }
    public void setDescription(String description) { this.description = description; }

    public Long getCreatorId() { return creatorId; }
    public void setCreatorId(Long creatorId) { this.creatorId = creatorId; }

    public LocalDateTime getCreatedAt() { return createdAt; }
    public void setCreatedAt(LocalDateTime createdAt) { this.createdAt = createdAt; }

    public boolean isPrivate() { return isPrivate; }
    public void setPrivate(boolean aPrivate) { isPrivate = aPrivate; }

    public boolean isActive() { return active; }
    public void setActive(boolean active) { this.active = active; }
}