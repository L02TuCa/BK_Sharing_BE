package app.mobile.BK_sharing.document.dto;

import app.mobile.BK_sharing.document.entity.Document;
import app.mobile.BK_sharing.document.entity.DocumentVersion;
import lombok.Data;

import java.time.LocalDateTime;
import java.util.List;

@Data
public class DocumentResponseDto {
    private Long documentId;
    private String title;
    private String description;
    private String fileType;
    private String filePath;
    private Long fileSize;
    private Long uploadedById;
    private String uploadedByUsername;
    private LocalDateTime createdAt;
    private List<DocumentVersion> versions; // Add versions
    private Integer versionCount;

    // Add constructor from Document entity
    public DocumentResponseDto(Document document) {
        this.documentId = document.getDocumentId();
        this.title = document.getTitle();
        this.description = document.getDescription();
        this.fileType = document.getFileType().name();
        this.filePath = document.getFilePath();
        this.fileSize = document.getFileSize();
        this.createdAt = document.getCreatedAt();


        // Only get necessary user info
        if (document.getUploadedBy() != null) {
            this.uploadedById = document.getUploadedBy().getUserId();
            this.uploadedByUsername = document.getUploadedBy().getUsername();
        }
    }
}
