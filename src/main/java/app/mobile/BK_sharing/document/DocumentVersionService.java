package app.mobile.BK_sharing.document.service;

import app.mobile.BK_sharing.document.entity.Document;
import app.mobile.BK_sharing.document.entity.DocumentVersion;
import app.mobile.BK_sharing.document.repository.DocumentVersionRepository;
import app.mobile.BK_sharing.user.User;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;

@Service
@RequiredArgsConstructor
@Transactional
public class DocumentVersionService {

    private final DocumentVersionRepository documentVersionRepository;

    public DocumentVersion createVersion(
            Document document,
            User editedBy,
            String filePath,
            String changeDescription,
            Long fileSize) {

        // Get next version number
        Integer nextVersionNumber = getNextVersionNumber(document.getDocumentId());

        DocumentVersion version = new DocumentVersion();
        version.setDocument(document);
        version.setVersionNumber(nextVersionNumber);
        version.setFilePath(filePath);
        version.setEditedBy(editedBy);
        version.setChangeDescription(changeDescription);
        version.setFileSize(fileSize);
        version.setEditedAt(LocalDateTime.now());

        return documentVersionRepository.save(version);
    }

    public void createInitialVersion(
            Document document,
            User uploadedBy,
            String filePath,
            Long fileSize) {

        createVersion(
                document,
                uploadedBy,
                filePath,
                "Initial upload",
                fileSize
        );
    }

    @Transactional(readOnly = true)
    public List<DocumentVersion> getVersionsByDocumentId(Long documentId) {
        return documentVersionRepository.findByDocumentDocumentIdOrderByVersionNumberDesc(documentId);
    }

    @Transactional(readOnly = true)
    public DocumentVersion getVersionById(Long versionId) {
        return documentVersionRepository.findById(versionId)
                .orElseThrow(() -> new EntityNotFoundException("Document version not found with ID: " + versionId));
    }

    @Transactional(readOnly = true)
    public DocumentVersion getLatestVersion(Long documentId) {
        return documentVersionRepository.findTopByDocumentDocumentIdOrderByVersionNumberDesc(documentId)
                .orElseThrow(() -> new EntityNotFoundException("No versions found for document ID: " + documentId));
    }

    @Transactional(readOnly = true)
    public int getVersionCount(Long documentId) {
        return documentVersionRepository.countByDocumentDocumentId(documentId);
    }

    private Integer getNextVersionNumber(Long documentId) {
        Integer latestVersion = documentVersionRepository.findMaxVersionNumberByDocumentId(documentId);
        return (latestVersion == null) ? 1 : latestVersion + 1;
    }

    // Rollback to a previous version
    public DocumentVersion rollbackToVersion(Long documentId, Integer versionNumber, User rolledBackBy) {
        DocumentVersion targetVersion = documentVersionRepository
                .findByDocumentDocumentIdAndVersionNumber(documentId, versionNumber)
                .orElseThrow(() -> new EntityNotFoundException(
                        "Version " + versionNumber + " not found for document ID: " + documentId));

        // Create a new version with the rolled-back content
        return createVersion(
                targetVersion.getDocument(),
                rolledBackBy,
                targetVersion.getFilePath(),
                "Rolled back to version " + versionNumber,
                targetVersion.getFileSize()
        );
    }
}