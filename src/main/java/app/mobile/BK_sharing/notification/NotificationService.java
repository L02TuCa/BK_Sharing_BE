package app.mobile.BK_sharing.notification;

import app.mobile.BK_sharing.document.entity.Document;
import app.mobile.BK_sharing.user.User;

import java.util.List;

public interface NotificationService {
    Notification createNotification(User user, String title, String message,
                                    Notification.NotificationType type, Document relatedDocument);
    Notification createNotification(User user, String title, String message,
                                    Notification.NotificationType type);
    List<Notification> getUserNotifications(Long userId);
    List<Notification> getNotificationsByType(Notification.NotificationType type);
    Notification getNotificationById(Long notificationId);
    void deleteNotification(Long notificationId);
    void deleteAllUserNotifications(Long userId);
}
