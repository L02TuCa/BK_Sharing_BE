package app.mobile.BK_sharing.notification;

import app.mobile.BK_sharing.exception.ResourceNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/notifications")
@RequiredArgsConstructor
//@Tag(name = "Notification", description = "Notification management APIs")
public class NotificationController {

    private final NotificationService notificationService;

    /**
     * Get all notifications for the authenticated user
     */
    @GetMapping("/{userId}")
//    @Operation(summary = "Get all notifications for user")
    public ResponseEntity<Map<String, Object>> getUserNotifications(@PathVariable Long userId) {
        List<Notification> notifications = notificationService.getUserNotifications(userId);

        Map<String, Object> response = new HashMap<>();
        response.put("success", true);
        response.put("data", notifications);
        response.put("count", notifications.size());

        return ResponseEntity.ok(response);
    }

//    /**
//     * Get unread notifications for the authenticated user
//     */
//    @GetMapping("/unread")
//    @Operation(summary = "Get unread notifications")
//    public ResponseEntity<Map<String, Object>> getUnreadNotifications(@AuthenticationPrincipal Long userId) {
//        List<Notification> unreadNotifications = notificationService.getUnreadNotifications(userId);
//
//        Map<String, Object> response = new HashMap<>();
//        response.put("success", true);
//        response.put("data", unreadNotifications);
//        response.put("count", unreadNotifications.size());
//
//        return ResponseEntity.ok(response);
//    }

//    /**
//     * Get notification statistics
//     */
//    @GetMapping("/stats")
//    @Operation(summary = "Get notification statistics")
//    public ResponseEntity<Map<String, Object>> getNotificationStats(@AuthenticationPrincipal Long userId) {
//        NotificationStats stats = notificationService.getNotificationStats(userId);
//
//        Map<String, Object> response = new HashMap<>();
//        response.put("success", true);
//        response.put("data", stats);
//
//        return ResponseEntity.ok(response);
//    }

//    /**
//     * Mark a specific notification as read
//     */
//    @PatchMapping("/{notificationId}/read")
//    @Operation(summary = "Mark notification as read")
//    public ResponseEntity<Map<String, Object>> markAsRead(
//            @PathVariable Long notificationId,
//            @AuthenticationPrincipal Long userId) {
//
//        try {
//            // Verify the notification belongs to the user
//            Notification notification = notificationService.getNotificationById(notificationId);
//            if (!notification.getUser().getUserId().equals(userId)) {
//                return ResponseEntity.status(HttpStatus.FORBIDDEN)
//                        .body(createErrorResponse("You don't have permission to access this notification"));
//            }
//
//            Notification updatedNotification = notificationService.markAsRead(notificationId);
//
//            Map<String, Object> response = new HashMap<>();
//            response.put("success", true);
//            response.put("message", "Notification marked as read");
//            response.put("data", updatedNotification);
//
//            return ResponseEntity.ok(response);
//
//        } catch (NotificationService.NotificationNotFoundException e) {
//            return ResponseEntity.status(HttpStatus.NOT_FOUND)
//                    .body(createErrorResponse(e.getMessage()));
//        }
//    }

//    /**
//     * Mark all notifications as read for the authenticated user
//     */
//    @PatchMapping("/mark-all-read")
//    @Operation(summary = "Mark all notifications as read")
//    public ResponseEntity<Map<String, Object>> markAllAsRead(@AuthenticationPrincipal Long userId) {
//        notificationService.markAllAsRead(userId);
//
//        Map<String, Object> response = new HashMap<>();
//        response.put("success", true);
//        response.put("message", "All notifications marked as read");
//
//        return ResponseEntity.ok(response);
//    }

    /**
     * Delete a specific notification
     */
    @DeleteMapping("/{userId}/{notificationId}")
    public ResponseEntity<Map<String, Object>> deleteNotification(
            @PathVariable Long notificationId,
            @PathVariable Long userId) {

        try {
            // Verify the notification belongs to the user
            Notification notification = notificationService.getNotificationById(notificationId);
            if (!notification.getUser().getUserId().equals(userId)) {
                return ResponseEntity.status(HttpStatus.FORBIDDEN)
                        .body(createErrorResponse("You don't have permission to delete this notification"));
            }

            notificationService.deleteNotification(notificationId);

            Map<String, Object> response = new HashMap<>();
            response.put("success", true);
            response.put("message", "Notification deleted successfully");

            return ResponseEntity.ok(response);

        } catch (ResourceNotFoundException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body(createErrorResponse(e.getMessage()));
        }
    }

    /**
     * Delete all notifications for the authenticated user
     */
    @DeleteMapping("/{userId}/all")
//    @Operation(summary = "Delete all notifications")
    public ResponseEntity<Map<String, Object>> deleteAllNotifications(@AuthenticationPrincipal Long userId) {
        notificationService.deleteAllUserNotifications(userId);

        Map<String, Object> response = new HashMap<>();
        response.put("success", true);
        response.put("message", "All notifications deleted successfully");

        return ResponseEntity.ok(response);
    }

    /**
     * Helper method to create error response
     */
    private Map<String, Object> createErrorResponse(String message) {
        Map<String, Object> errorResponse = new HashMap<>();
        errorResponse.put("success", false);
        errorResponse.put("message", message);
        return errorResponse;
    }
}