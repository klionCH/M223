package com.example.demo.controller;

import com.example.demo.entity.Notification;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

@RestController
@RequestMapping("/notifications")
@CrossOrigin(origins = "*")
public class NotificationController {

    private static List<Notification> notifications = new ArrayList<>();

    static {
        // Static sample notifications
        Notification notif1 = new Notification(1L, 1L, Notification.NotificationType.LIKE,
                "Jane liked your photo");
        notif1.setRelatedPostId(1L);
        notif1.setRelatedUserId(2L);

        Notification notif2 = new Notification(2L, 1L, Notification.NotificationType.COMMENT,
                "Mike commented on your post");
        notif2.setRelatedPostId(1L);
        notif2.setRelatedUserId(3L);

        Notification notif3 = new Notification(3L, 2L, Notification.NotificationType.FOLLOW,
                "John started following you");
        notif3.setRelatedUserId(1L);

        Notification notif4 = new Notification(4L, 1L, Notification.NotificationType.GROUP_INVITE,
                "You've been invited to join Creative Photographers");
        notif4.setRead(true);

        notifications.add(notif1);
        notifications.add(notif2);
        notifications.add(notif3);
        notifications.add(notif4);
    }

    @GetMapping
    public ResponseEntity<List<Notification>> getAllNotifications(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int limit,
            @RequestParam(required = false) Boolean isRead) {

        // Simulate current user
        Long currentUserId = 1L;

        List<Notification> userNotifications = notifications.stream()
                .filter(n -> n.getUserId().equals(currentUserId))
                .filter(n -> isRead == null || n.isRead() == isRead)
                .skip(page * limit)
                .limit(limit)
                .toList();

        return ResponseEntity.ok(userNotifications);
    }

    @PutMapping("/{id}")
    public ResponseEntity<Map<String, Object>> markAsRead(@PathVariable Long id) {
        Optional<Notification> notificationOpt = notifications.stream()
                .filter(n -> n.getId().equals(id))
                .findFirst();

        if (notificationOpt.isPresent()) {
            Notification notification = notificationOpt.get();
            notification.setRead(true);

            Map<String, Object> response = new HashMap<>();
            response.put("success", true);
            response.put("message", "Notification marked as read");

            return ResponseEntity.ok(response);
        } else {
            return ResponseEntity.notFound().build();
        }
    }

    @PutMapping("/mark-all-read")
    public ResponseEntity<Map<String, Object>> markAllAsRead() {
        // Simulate current user
        Long currentUserId = 1L;

        long updatedCount = notifications.stream()
                .filter(n -> n.getUserId().equals(currentUserId))
                .filter(n -> !n.isRead())
                .peek(n -> n.setRead(true))
                .count();

        Map<String, Object> response = new HashMap<>();
        response.put("success", true);
        response.put("message", "All notifications marked as read");
        response.put("updatedCount", updatedCount);

        return ResponseEntity.ok(response);
    }

    @GetMapping("/unread-count")
    public ResponseEntity<Map<String, Long>> getUnreadCount() {
        // Simulate current user
        Long currentUserId = 1L;

        long unreadCount = notifications.stream()
                .filter(n -> n.getUserId().equals(currentUserId))
                .filter(n -> !n.isRead())
                .count();

        Map<String, Long> response = new HashMap<>();
        response.put("unreadCount", unreadCount);

        return ResponseEntity.ok(response);
    }

    @PostMapping
    public ResponseEntity<Notification> createNotification(@RequestBody Notification notification) {
        notification.setId((long) (notifications.size() + 1));
        notifications.add(notification);

        return ResponseEntity.ok(notification);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteNotification(@PathVariable Long id) {
        boolean removed = notifications.removeIf(n -> n.getId().equals(id));

        if (removed) {
            return ResponseEntity.noContent().build();
        } else {
            return ResponseEntity.notFound().build();
        }
    }
}
