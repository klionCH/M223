package com.example.demo.entity;

import java.time.LocalDateTime;

public class GroupMember {
    private Long id;
    private Long groupId;
    private Long userId;
    private Role role;
    private LocalDateTime joinedAt;

    public enum Role {
        ADMIN, MEMBER
    }

    // Constructors
    public GroupMember() {}

    public GroupMember(Long id, Long groupId, Long userId, Role role) {
        this.id = id;
        this.groupId = groupId;
        this.userId = userId;
        this.role = role;
        this.joinedAt = LocalDateTime.now();
    }

    // Getters and Setters
    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public Long getGroupId() { return groupId; }
    public void setGroupId(Long groupId) { this.groupId = groupId; }

    public Long getUserId() { return userId; }
    public void setUserId(Long userId) { this.userId = userId; }

    public Role getRole() { return role; }
    public void setRole(Role role) { this.role = role; }

    public LocalDateTime getJoinedAt() { return joinedAt; }
    public void setJoinedAt(LocalDateTime joinedAt) { this.joinedAt = joinedAt; }
}
