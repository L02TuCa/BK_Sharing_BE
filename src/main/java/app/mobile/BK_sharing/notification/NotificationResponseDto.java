package app.mobile.BK_sharing.notification;

import app.mobile.BK_sharing.notification.Notification;
import lombok.Data;

import java.time.LocalDateTime;

@Data
public class NotificationResponseDto {
    private Long notificationId;
    private String title;
    private String message;
    private Notification.NotificationType type;
    private Boolean isRead;
    private LocalDateTime createdAt;
    private Long relatedDocumentId;

    public static NotificationResponseDto fromEntity(Notification notification) {
        NotificationResponseDto response = new NotificationResponseDto();
        response.setNotificationId(notification.getNotificationId());
        response.setTitle(notification.getTitle());
        response.setMessage(notification.getMessage());
        response.setType(notification.getType());
        response.setIsRead(notification.getIsRead());
        response.setCreatedAt(notification.getCreatedAt());

        if (notification.getRelatedDocument() != null) {
            response.setRelatedDocumentId(notification.getRelatedDocument().getDocumentId());
        }

        return response;
    }
}