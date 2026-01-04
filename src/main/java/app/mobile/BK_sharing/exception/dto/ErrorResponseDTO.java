package app.mobile.BK_sharing.exception.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class ErrorResponseDTO {
    private boolean success;
    private String message;
    private String entityType; // "User", "Category", "Document", etc.
    private Object errors; // Can be Map<String, String> or List<String>
}