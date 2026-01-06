package app.mobile.BK_sharing.notification;

import app.mobile.BK_sharing.document.entity.Document;
import app.mobile.BK_sharing.exception.ResourceNotFoundException;
import app.mobile.BK_sharing.user.User;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Transactional
public class NotificationServiceImpl {

    private final NotificationRepository notificationRepository;

    /**
     * Create a new notification
     */
    public Notification createNotification(User user, String title, String message,
                                           Notification.NotificationType type, Document relatedDocument) {
        Notification notification = new Notification();
        notification.setUser(user);
        notification.setTitle(title);
        notification.setMessage(message);
        notification.setType(type);
        notification.setRelatedDocument(relatedDocument);
        notification.setIsRead(false);

        return notificationRepository.save(notification);
    }

    /**
     * Create a notification without related document
     */
    public Notification createNotification(User user, String title, String message,
                                           Notification.NotificationType type) {
        return createNotification(user, title, message, type, null);
    }

    /**
     * Get all notifications for a user
     */
    @Transactional(readOnly = true)
    public List<Notification> getUserNotifications(Long userId) {
        return notificationRepository.findByUserUserId(userId);
    }
//
//    /**
//     * Get unread notifications for a user
//     */
//    @Transactional(readOnly = true)
//    public List<Notification> getUnreadNotifications(Long userId) {
//        return notificationRepository.findByUserUserIdAndIsRead(userId, false);
//    }
//
//    /**
//     * Mark a notification as read
//     */
//    public Notification markAsRead(Long notificationId) {
//        Notification notification = notificationRepository.findById(notificationId)
//                .orElseThrow(() -> new NotificationNotFoundException("Notification not found with id: " + notificationId));
//
//        notification.setIsRead(true);
//        return notificationRepository.save(notification);
//    }
//
//    /**
//     * Mark all user notifications as read
//     */
//    public void markAllAsRead(Long userId) {
//        List<Notification> unreadNotifications = getUnreadNotifications(userId);
//        unreadNotifications.forEach(notification -> notification.setIsRead(true));
//        notificationRepository.saveAll(unreadNotifications);
//    }

    /**
     * Get notifications by type
     */
    @Transactional(readOnly = true)
    public List<Notification> getNotificationsByType(Notification.NotificationType type) {
        return notificationRepository.findByType(type);
    }

    /**
     * Get notification by ID
     */
    @Transactional(readOnly = true)
    public Notification getNotificationById(Long notificationId) {
        return notificationRepository.findById(notificationId)
                .orElseThrow(() -> new ResourceNotFoundException("Notification not found with id: " + notificationId));
    }

    /**
     * Delete a notification
     */
    public void deleteNotification(Long notificationId) {
        if (!notificationRepository.existsById(notificationId)) {
            throw new ResourceNotFoundException("Notification not found with id: " + notificationId);
        }
        notificationRepository.deleteById(notificationId);
    }

    /**
     * Delete all notifications for a user
     */
    public void deleteAllUserNotifications(Long userId) {
        List<Notification> userNotifications = notificationRepository.findByUserUserId(userId);
        notificationRepository.deleteAll(userNotifications);
    }

}