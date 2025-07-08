package ch.kbw.sl.controller;

import ch.kbw.sl.entity.Group;
import ch.kbw.sl.entity.GroupMember;
import ch.kbw.sl.entity.User;
import ch.kbw.sl.service.GroupService;
import ch.kbw.sl.service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/groups")
@RequiredArgsConstructor
@CrossOrigin(origins = "*")
public class GroupController {

    private final GroupService groupService;
    private final UserService userService;

    @GetMapping
    public ResponseEntity<Page<Group>> getAllGroups(Pageable pageable) {
        Page<Group> groups = groupService.findPublicGroups(pageable);
        return ResponseEntity.ok(groups);
    }

    @GetMapping("/search")
    public ResponseEntity<List<Group>> searchGroups(@RequestParam String query) {
        List<Group> groups = groupService.searchPublicGroups(query);
        return ResponseEntity.ok(groups);
    }

    @PostMapping
    public ResponseEntity<Group> createGroup(@RequestBody Group group) {
        User currentUser = userService.findById(1L).orElse(null);
        if (currentUser == null) {
            return ResponseEntity.badRequest().build();
        }

        try {
            Group savedGroup = groupService.createGroup(group, currentUser);
            return ResponseEntity.ok(savedGroup);
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().build();
        }
    }

    @GetMapping("/{id}")
    public ResponseEntity<Group> getGroupById(@PathVariable Long id) {
        return groupService.findById(id)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    @PutMapping("/{id}")
    public ResponseEntity<Group> updateGroup(@PathVariable Long id, @RequestBody Group group) {
        try {
            Group updatedGroup = groupService.updateGroup(id, group);
            return ResponseEntity.ok(updatedGroup);
        } catch (IllegalArgumentException e) {
            return ResponseEntity.notFound().build();
        }
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteGroup(@PathVariable Long id) {
        try {
            groupService.deleteGroup(id);
            return ResponseEntity.noContent().build();
        } catch (IllegalArgumentException e) {
            return ResponseEntity.notFound().build();
        }
    }

    @GetMapping("/{groupId}/members")
    public ResponseEntity<List<GroupMember>> getGroupMembers(@PathVariable Long groupId) {
        Group group = groupService.findById(groupId).orElse(null);
        if (group == null) {
            return ResponseEntity.notFound().build();
        }

        List<GroupMember> members = groupService.getGroupMembers(group);
        return ResponseEntity.ok(members);
    }

    @PostMapping("/{groupId}/join")
    public ResponseEntity<Map<String, Object>> joinGroup(@PathVariable Long groupId) {
        User currentUser = userService.findById(1L).orElse(null);
        Group group = groupService.findById(groupId).orElse(null);

        if (currentUser == null || group == null) {
            return ResponseEntity.badRequest().build();
        }

        try {
            GroupMember member = groupService.joinGroup(group, currentUser);

            Map<String, Object> response = new HashMap<>();
            response.put("success", true);
            response.put("message", "Successfully joined group");
            response.put("memberId", member.getId());

            return ResponseEntity.ok(response);
        } catch (IllegalArgumentException e) {
            Map<String, Object> response = new HashMap<>();
            response.put("success", false);
            response.put("message", e.getMessage());

            return ResponseEntity.badRequest().body(response);
        }
    }

    @DeleteMapping("/{groupId}/leave")
    public ResponseEntity<Map<String, String>> leaveGroup(@PathVariable Long groupId) {
        User currentUser = userService.findById(1L).orElse(null);
        Group group = groupService.findById(groupId).orElse(null);

        if (currentUser == null || group == null) {
            return ResponseEntity.badRequest().build();
        }

        groupService.leaveGroup(group, currentUser);

        Map<String, String> response = new HashMap<>();
        response.put("message", "Successfully left group");

        return ResponseEntity.ok(response);
    }

    @PutMapping("/{groupId}/members/{userId}")
    public ResponseEntity<Map<String, Object>> updateMemberRole(
            @PathVariable Long groupId,
            @PathVariable Long userId,
            @RequestBody Map<String, String> roleUpdate) {

        Group group = groupService.findById(groupId).orElse(null);
        User user = userService.findById(userId).orElse(null);

        if (group == null || user == null) {
            return ResponseEntity.badRequest().build();
        }

        try {
            GroupMember.Role role = GroupMember.Role.valueOf(roleUpdate.get("role"));
            GroupMember updatedMember = groupService.updateMemberRole(group, user, role);

            Map<String, Object> response = new HashMap<>();
            response.put("success", true);
            response.put("message", "Member role updated successfully");
            response.put("newRole", updatedMember.getRole());

            return ResponseEntity.ok(response);
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().build();
        }
    }
}
