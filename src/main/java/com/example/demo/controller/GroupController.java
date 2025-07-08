package com.example.demo.controller;

import com.example.demo.entity.Group;
import com.example.demo.entity.GroupMember;
import com.example.demo.entity.Post;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

@RestController
@RequestMapping("/groups")
@CrossOrigin(origins = "*")
public class GroupController {

    private static List<Group> groups = new ArrayList<>();
    private static List<GroupMember> groupMembers = new ArrayList<>();

    static {
        // Static sample groups
        Group group1 = new Group(1L, "Creative Photographers",
                "A community for photography enthusiasts", 1L, false);
        Group group2 = new Group(2L, "UI/UX Designers",
                "Share and discuss design trends", 2L, false);
        Group group3 = new Group(3L, "Video Creators Club",
                "Collaboration space for video creators", 3L, true);

        groups.add(group1);
        groups.add(group2);
        groups.add(group3);

        // Static sample group members
        groupMembers.add(new GroupMember(1L, 1L, 1L, GroupMember.Role.ADMIN));
        groupMembers.add(new GroupMember(2L, 1L, 2L, GroupMember.Role.MEMBER));
        groupMembers.add(new GroupMember(3L, 1L, 3L, GroupMember.Role.MEMBER));
        groupMembers.add(new GroupMember(4L, 2L, 2L, GroupMember.Role.ADMIN));
        groupMembers.add(new GroupMember(5L, 2L, 1L, GroupMember.Role.MEMBER));
        groupMembers.add(new GroupMember(6L, 3L, 3L, GroupMember.Role.ADMIN));
    }

    @GetMapping
    public ResponseEntity<List<Group>> getAllGroups(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int limit) {

        List<Group> publicGroups = groups.stream()
                .filter(g -> !g.isPrivate())
                .skip(page * limit)
                .limit(limit)
                .toList();

        return ResponseEntity.ok(publicGroups);
    }

    @PostMapping
    public ResponseEntity<Group> createGroup(@RequestBody Group group) {
        group.setId((long) (groups.size() + 1));
        group.setCreatorId(1L); // Simulate current user
        groups.add(group);

        // Add creator as admin member
        GroupMember creatorMember = new GroupMember(
                (long) (groupMembers.size() + 1),
                group.getId(),
                group.getCreatorId(),
                GroupMember.Role.ADMIN
        );
        groupMembers.add(creatorMember);

        return ResponseEntity.ok(group);
    }

    @GetMapping("/{id}")
    public ResponseEntity<Group> getGroupById(@PathVariable Long id) {
        Optional<Group> group = groups.stream()
                .filter(g -> g.getId().equals(id))
                .findFirst();

        if (group.isPresent()) {
            return ResponseEntity.ok(group.get());
        } else {
            return ResponseEntity.notFound().build();
        }
    }

    @PutMapping("/{id}")
    public ResponseEntity<Group> updateGroup(@PathVariable Long id, @RequestBody Group updatedGroup) {
        Optional<Group> groupOpt = groups.stream()
                .filter(g -> g.getId().equals(id))
                .findFirst();

        if (groupOpt.isPresent()) {
            Group group = groupOpt.get();
            group.setName(updatedGroup.getName());
            group.setDescription(updatedGroup.getDescription());
            group.setPrivate(updatedGroup.isPrivate());
            return ResponseEntity.ok(group);
        } else {
            return ResponseEntity.notFound().build();
        }
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteGroup(@PathVariable Long id) {
        boolean removed = groups.removeIf(g -> g.getId().equals(id));

        if (removed) {
            // Also remove all group members
            groupMembers.removeIf(gm -> gm.getGroupId().equals(id));
            return ResponseEntity.noContent().build();
        } else {
            return ResponseEntity.notFound().build();
        }
    }

    @GetMapping("/{groupId}/members")
    public ResponseEntity<List<Map<String, Object>>> getGroupMembers(@PathVariable Long groupId) {
        List<Map<String, Object>> members = groupMembers.stream()
                .filter(gm -> gm.getGroupId().equals(groupId))
                .map(gm -> {
                    Map<String, Object> member = new HashMap<>();
                    member.put("userId", gm.getUserId());
                    member.put("role", gm.getRole());
                    member.put("joinedAt", gm.getJoinedAt());
                    // In real app, would fetch user details
                    member.put("username", "user_" + gm.getUserId());
                    return member;
                })
                .toList();

        return ResponseEntity.ok(members);
    }

    @PostMapping("/{groupId}/join")
    public ResponseEntity<Map<String, Object>> joinGroup(@PathVariable Long groupId) {
        // Simulate current user
        Long currentUserId = 1L;

        // Check if already a member
        boolean alreadyMember = groupMembers.stream()
                .anyMatch(gm -> gm.getGroupId().equals(groupId) &&
                        gm.getUserId().equals(currentUserId));

        if (alreadyMember) {
            Map<String, Object> response = new HashMap<>();
            response.put("success", false);
            response.put("message", "Already a member of this group");
            return ResponseEntity.badRequest().body(response);
        }

        GroupMember newMember = new GroupMember(
                (long) (groupMembers.size() + 1),
                groupId,
                currentUserId,
                GroupMember.Role.MEMBER
        );
        groupMembers.add(newMember);

        Map<String, Object> response = new HashMap<>();
        response.put("success", true);
        response.put("message", "Successfully joined group");
        response.put("memberId", newMember.getId());

        return ResponseEntity.ok(response);
    }

    @DeleteMapping("/{groupId}/leave")
    public ResponseEntity<Map<String, String>> leaveGroup(@PathVariable Long groupId) {
        // Simulate current user
        Long currentUserId = 1L;

        boolean removed = groupMembers.removeIf(gm ->
                gm.getGroupId().equals(groupId) &&
                        gm.getUserId().equals(currentUserId));

        Map<String, String> response = new HashMap<>();
        if (removed) {
            response.put("message", "Successfully left group");
            return ResponseEntity.ok(response);
        } else {
            response.put("message", "Membership not found");
            return ResponseEntity.notFound().build();
        }
    }

    @PutMapping("/{groupId}/members/{userId}")
    public ResponseEntity<Map<String, Object>> updateMemberRole(
            @PathVariable Long groupId,
            @PathVariable Long userId,
            @RequestBody Map<String, String> roleUpdate) {

        Optional<GroupMember> memberOpt = groupMembers.stream()
                .filter(gm -> gm.getGroupId().equals(groupId) &&
                        gm.getUserId().equals(userId))
                .findFirst();

        if (memberOpt.isPresent()) {
            GroupMember member = memberOpt.get();
            member.setRole(GroupMember.Role.valueOf(roleUpdate.get("role")));

            Map<String, Object> response = new HashMap<>();
            response.put("success", true);
            response.put("message", "Member role updated successfully");
            response.put("newRole", member.getRole());

            return ResponseEntity.ok(response);
        } else {
            return ResponseEntity.notFound().build();
        }
    }

    @GetMapping("/{groupId}/posts")
    public ResponseEntity<List<Map<String, Object>>> getGroupPosts(
            @PathVariable Long groupId,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int limit) {

        // Simulate group posts (in real app, would filter posts by group)
        List<Map<String, Object>> groupPosts = new ArrayList<>();

        Map<String, Object> post1 = new HashMap<>();
        post1.put("id", 1L);
        post1.put("title", "Group Photography Challenge");
        post1.put("content", "This week's theme: Street Photography");
        post1.put("userId", 1L);
        post1.put("groupId", groupId);

        Map<String, Object> post2 = new HashMap<>();
        post2.put("id", 2L);
        post2.put("title", "Equipment Discussion");
        post2.put("content", "What's your favorite lens for portraits?");
        post2.put("userId", 2L);
        post2.put("groupId", groupId);

        groupPosts.add(post1);
        groupPosts.add(post2);

        return ResponseEntity.ok(groupPosts);
    }
}