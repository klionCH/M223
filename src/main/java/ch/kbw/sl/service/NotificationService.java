package ch.kbw.sl.service;

import ch.kbw.sl.entity.Notification;
import ch.kbw.sl.entity.User;
import ch.kbw.sl.repository.NotificationRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
@Transactional
public class NotificationService {

    private final NotificationRepository notificationRepository;

    public Notification createNotification(Notification notification) {
        return notificationRepository.save(notification);
    }

    @Transactional(readOnly = true)
    public Page<Notification> findByUser(User user, Pageable pageable) {
        return notificationRepository.findByUserOrderByCreatedAtDesc(user, pageable);
    }

    @Transactional(readOnly = true)
    public Page<Notification> findByUserAndReadStatus(User user, Boolean isRead, Pageable pageable) {
        return notificationRepository.findByUserAndIsReadOrderByCreatedAtDesc(user, isRead, pageable);
    }

    @Transactional(readOnly = true)
    public long getUnreadCount(User user) {
        return notificationRepository.countByUserAndIsReadFalse(user);
    }

    public Notification markAsRead(Long id) {
        Notification notification = notificationRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Notification not found"));

        notification.setIsRead(true);
        return notificationRepository.save(notification);
    }

    public int markAllAsRead(User user) {
        return notificationRepository.markAllAsReadForUser(user);
    }

    public void deleteNotification(Long id) {
        notificationRepository.deleteById(id);
    }
}
