package ch.kbw.sl.controller;
import ch.kbw.sl.entity.Notification;
import ch.kbw.sl.entity.User;
import ch.kbw.sl.service.NotificationService;
import ch.kbw.sl.service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping("/api/notifications")
@RequiredArgsConstructor
@CrossOrigin(origins = "*")
public class NotificationController {

    private final NotificationService notificationService;
    private final UserService userService;

    @GetMapping
    public ResponseEntity<Page<Notification>> getAllNotifications(
            Pageable pageable,
            @RequestParam(required = false) Boolean isRead) {

        User currentUser = userService.findById(1L).orElse(null);
        if (currentUser == null) {
            return ResponseEntity.badRequest().build();
        }

        Page<Notification> notifications = isRead != null
                ? notificationService.findByUserAndReadStatus(currentUser, isRead, pageable)
                : notificationService.findByUser(currentUser, pageable);

        return ResponseEntity.ok(notifications);
    }

    @PostMapping
    public ResponseEntity<Notification> createNotification(@RequestBody Notification notification) {
        Notification savedNotification = notificationService.createNotification(notification);
        return ResponseEntity.ok(savedNotification);
    }

    @PutMapping("/{id}")
    public ResponseEntity<Map<String, Object>> markAsRead(@PathVariable Long id) {
        try {
            Notification notification = notificationService.markAsRead(id);

            Map<String, Object> response = new HashMap<>();
            response.put("success", true);
            response.put("message", "Notification marked as read");

            return ResponseEntity.ok(response);
        } catch (IllegalArgumentException e) {
            return ResponseEntity.notFound().build();
        }
    }

    @PutMapping("/mark-all-read")
    public ResponseEntity<Map<String, Object>> markAllAsRead() {
        User currentUser = userService.findById(1L).orElse(null);
        if (currentUser == null) {
            return ResponseEntity.badRequest().build();
        }

        int updatedCount = notificationService.markAllAsRead(currentUser);

        Map<String, Object> response = new HashMap<>();
        response.put("success", true);
        response.put("message", "All notifications marked as read");
        response.put("updatedCount", updatedCount);

        return ResponseEntity.ok(response);
    }

    @GetMapping("/unread-count")
    public ResponseEntity<Map<String, Long>> getUnreadCount() {
        User currentUser = userService.findById(1L).orElse(null);
        if (currentUser == null) {
            return ResponseEntity.badRequest().build();
        }

        long unreadCount = notificationService.getUnreadCount(currentUser);

        Map<String, Long> response = new HashMap<>();
        response.put("unreadCount", unreadCount);

        return ResponseEntity.ok(response);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteNotification(@PathVariable Long id) {
        notificationService.deleteNotification(id);
        return ResponseEntity.noContent().build();
    }
}