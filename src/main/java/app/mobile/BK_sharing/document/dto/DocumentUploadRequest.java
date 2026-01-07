package app.mobile.BK_sharing.document.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Data;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

@Data
public class DocumentUploadRequest {
    @NotNull(message = "File is required")
    private MultipartFile file;

    @NotBlank(message = "Title is required")
    private String title;

    private String description;

    private List<Long> categoryIds; // Multiple categories

    private Long courseId; // Single course

    @NotNull(message = "User ID is required")
    private Long userId;
}
