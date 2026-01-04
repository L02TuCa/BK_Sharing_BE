package app.mobile.BK_sharing.notification;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface NotificationRepository extends JpaRepository<Notification, Long> {
    List<Notification> findByUserUserId(Long userId);
    List<Notification> findByUserUserIdAndIsRead(Long userId, Boolean isRead);
    List<Notification> findByType(Notification.NotificationType type);
}