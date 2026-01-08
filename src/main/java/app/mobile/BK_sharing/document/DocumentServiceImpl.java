package app.mobile.BK_sharing.document;

import app.mobile.BK_sharing.category.Category;
import app.mobile.BK_sharing.category.CategoryRepository;
import app.mobile.BK_sharing.course.Course;
import app.mobile.BK_sharing.course.CourseRepository;
import app.mobile.BK_sharing.document.dto.DocumentResponseDto;
import app.mobile.BK_sharing.document.entity.Document;
import app.mobile.BK_sharing.document.entity.DocumentVersion;
import app.mobile.BK_sharing.document.repository.DocumentRepository;
import app.mobile.BK_sharing.document.repository.DocumentVersionRepository;
import app.mobile.BK_sharing.storage.SupabaseStorageService;
import app.mobile.BK_sharing.user.User;
import app.mobile.BK_sharing.user.UserRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Slf4j
public class DocumentServiceImpl implements DocumentService {
    private final DocumentRepository documentRepository;
    private final SupabaseStorageService storageService;
    private final UserRepository userRepository; // To fetch the uploader
    private final CategoryRepository categoryRepository;
    private final CourseRepository courseRepository;
    private final DocumentVersionRepository documentVersionRepository;
    private final app.mobile.BK_sharing.document.service.DocumentVersionService documentVersionService;

    @Override
    @Transactional
    public DocumentResponseDto uploadDocument(
            MultipartFile file,
            String title,
            String description,
            Long userId,
            List<Long> categoryIds,
            Long courseId) {

        try {
            // 1. Basic validation
            if (file.isEmpty()) {
                throw new RuntimeException("File is empty");
            }

            // 2. Upload to Supabase and get PUBLIC URL
            String publicUrl = storageService.uploadFile(file);

            // 3. Create Document entity
            Document doc = new Document();
            doc.setTitle(title);
            doc.setDescription(description);
            doc.setFilePath(publicUrl);
            doc.setFileSize(file.getSize());

            // 4. Set file type
            String originalFilename = file.getOriginalFilename();
            assert originalFilename != null;
            String fileExtension = originalFilename.substring(originalFilename.lastIndexOf(".") + 1).toLowerCase();

            switch (fileExtension) {
                case "pdf" -> doc.setFileType(Document.FileType.PDF);
                case "docx", "doc" -> doc.setFileType(Document.FileType.WORD);
                case "pptx", "ppt" -> doc.setFileType(Document.FileType.POWERPOINT);
                default -> doc.setFileType(Document.FileType.OTHER);
            }

            // 5. Set uploader
            User uploader = userRepository.findById(userId)
                    .orElseThrow(() -> new RuntimeException("User not found with ID: " + userId));
            doc.setUploadedBy(uploader);
            doc.setIsApproved(false); // Initially not approved

            // 6. Initialize collections
            doc.setCategories(new ArrayList<>());

            // 7. Save document first (to get ID)
            documentRepository.save(doc);

            // 8. Handle categories (ManyToMany)
            if (categoryIds != null && !categoryIds.isEmpty()) {
                List<Category> categories = categoryRepository.findAllById(categoryIds);

                // Validate all categories exist
                if (categories.size() != categoryIds.size()) {
                    throw new RuntimeException("One or more categories not found");
                }

                // Add categories to document
                doc.getCategories().addAll(categories);
            }

            // 9. Handle course (ManyToOne)
            if (courseId != null) {
                Course course = courseRepository.findById(courseId)
                        .orElseThrow(() -> new RuntimeException("Course not found with ID: " + courseId));
                doc.setCourse(course);
            }

            // 10. Save with associations
            Document savedDocument = documentRepository.save(doc);

            // 11. Create initial document version
            documentVersionService.createInitialVersion(
                    savedDocument,
                    uploader,
                    publicUrl,
                    file.getSize()
            );

            log.info("Document uploaded successfully with version 1: {}", savedDocument.getDocumentId());
            return new DocumentResponseDto(savedDocument);

        } catch (IOException e) {
            throw new RuntimeException("Failed to upload file: " + e.getMessage(), e);
        }
    }

    @Override
    public DocumentResponseDto getDocumentById(Long id) {
        Document document = documentRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Document not found with ID: " + id));
        return new DocumentResponseDto(document);
    }

    @Override
    public List<DocumentResponseDto> getAllDocuments() {
        List<Document> documents = documentRepository.findAll();
        return documents.stream()
                .map(DocumentResponseDto::new)
                .collect(Collectors.toList());
    }

    @Override
    public Page<DocumentResponseDto> getAllDocuments(Pageable pageable) {
        return documentRepository.findAll(pageable)
                .map(DocumentResponseDto::new);
    }

    @Transactional
    public DocumentResponseDto updateDocumentWithFile(
            Long documentId,
            MultipartFile file,
            String changeDescription,
            Long userId) {

        try {
            // 1. Get existing document
            Document document = documentRepository.findById(documentId)
                    .orElseThrow(() -> new RuntimeException("Document not found with ID: " + documentId));

            // 2. Upload new file to Supabase
            String newPublicUrl = storageService.uploadFile(file);

            // 3. Get user who is making the update
            User editor = userRepository.findById(userId)
                    .orElseThrow(() -> new RuntimeException("User not found with ID: " + userId));

            // 4. Update document with new file info
            String originalFilename = file.getOriginalFilename();
            assert originalFilename != null;
            String fileExtension = originalFilename.substring(originalFilename.lastIndexOf(".") + 1).toLowerCase();

            switch (fileExtension) {
                case "pdf" -> document.setFileType(Document.FileType.PDF);
                case "docx", "doc" -> document.setFileType(Document.FileType.WORD);
                case "pptx", "ppt" -> document.setFileType(Document.FileType.POWERPOINT);
                default -> document.setFileType(Document.FileType.OTHER);
            }

            document.setFilePath(newPublicUrl);
            document.setFileSize(file.getSize());

            // Reset approval status when file is updated
            document.setIsApproved(false);
            document.setApprovedBy(null);

            Document updatedDocument = documentRepository.save(document);

            // 5. Create new document version
            documentVersionService.createVersion(
                    updatedDocument,
                    editor,
                    newPublicUrl,
                    changeDescription != null ? changeDescription : "Document file updated",
                    file.getSize()
            );

            log.info("Document file updated with new version for document ID: {}", documentId);
            return new DocumentResponseDto(updatedDocument);

        } catch (IOException e) {
            throw new RuntimeException("Failed to update document file: " + e.getMessage(), e);
        }
    }

    @Transactional
    public DocumentResponseDto updateDocumentMetadata(
            Long documentId,
            String title,
            String description,
            List<Long> categoryIds,
            Long courseId) {

        // 1. Get existing document
        Document document = documentRepository.findById(documentId)
                .orElseThrow(() -> new RuntimeException("Document not found with ID: " + documentId));

        // 2. Update title if provided
        if (title != null && !title.trim().isEmpty()) {
            document.setTitle(title);
        }

        // 3. Update description if provided
        if (description != null) {
            document.setDescription(description);
        }

        // 4. Update categories if provided
        if (categoryIds != null) {
            List<Category> categories = categoryRepository.findAllById(categoryIds);

            // Validate all categories exist
            if (categories.size() != categoryIds.size()) {
                throw new RuntimeException("One or more categories not found");
            }

            // Update categories
            document.setCategories(categories);
        }

        // 5. Update course if provided
        if (courseId != null) {
            Course course = courseRepository.findById(courseId)
                    .orElseThrow(() -> new RuntimeException("Course not found with ID: " + courseId));
            document.setCourse(course);
        } else if (courseId == null && document.getCourse() != null) {
            // Remove course association if courseId is explicitly null
            document.setCourse(null);
        }

        // 6. Save updated document
        Document updatedDocument = documentRepository.save(document);

        // 7. Create version entry for metadata change (optional)
        if (document.getUploadedBy() != null) {
            documentVersionService.createVersion(
                    updatedDocument,
                    document.getUploadedBy(),
                    document.getFilePath(),
                    "Metadata updated (title, description, categories, or course)",
                    document.getFileSize()
            );
        }

        log.info("Document metadata updated for document ID: {}", documentId);
        return new DocumentResponseDto(updatedDocument);
    }

    @Transactional
    public void deleteDocument(Long documentId, boolean deleteAllVersions) {
        // 1. Get existing document
        Document document = documentRepository.findById(documentId)
                .orElseThrow(() -> new RuntimeException("Document not found with ID: " + documentId));

        // 2. Delete from storage (optional - decide based on your requirements)
        try {
            storageService.deleteFile(document.getFilePath());

            // If deleting all versions, delete all version files too
            if (deleteAllVersions) {
                List<DocumentVersion> versions = documentVersionRepository
                        .findByDocumentDocumentIdOrderByVersionNumberDesc(documentId);

                for (DocumentVersion version : versions) {
                    if (!version.getFilePath().equals(document.getFilePath())) {
                        storageService.deleteFile(version.getFilePath());
                    }
                }
            }
        } catch (IOException e) {
            log.warn("Failed to delete file from storage: {}", e.getMessage());
            // Continue with database deletion even if storage deletion fails
        }

        // 3. Delete document versions if requested
        if (deleteAllVersions) {
            documentVersionRepository.deleteByDocumentDocumentId(documentId);
        }

        // 4. Delete document from database
        documentRepository.delete(document);

        log.info("Document deleted with ID: {}, deleteAllVersions: {}", documentId, deleteAllVersions);
    }

    // Get document with versions
    public DocumentResponseDto getDocumentWithVersions(Long documentId) {
        Document document = documentRepository.findById(documentId)
                .orElseThrow(() -> new RuntimeException("Document not found with ID: " + documentId));

        List<DocumentVersion> versions = documentVersionService.getVersionsByDocumentId(documentId);

        DocumentResponseDto response = new DocumentResponseDto(document);
        response.setVersions(versions);
        response.setVersionCount(versions.size());

        return response;
    }

    @Override
    public List<DocumentResponseDto> getDocumentsByUser(Long userId) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new RuntimeException("User not found with ID: " + userId));
        List<Document> documents = documentRepository.findByUploadedByUserId(user.getUserId());
        return documents.stream()
                .map(DocumentResponseDto::new)
                .collect(Collectors.toList());
    }

    @Override
    @Transactional
    public DocumentResponseDto approveDocument(Long documentId, Long approvedByUserId) {
        Document document = documentRepository.findById(documentId)
                .orElseThrow(() -> new RuntimeException("Document not found with ID: " + documentId));

        User approver = userRepository.findById(approvedByUserId)
                .orElseThrow(() -> new RuntimeException("Approver not found with ID: " + approvedByUserId));

        document.setIsApproved(true);
        document.setApprovedBy(approver);

        Document approved = documentRepository.save(document);
        log.info("Document approved. ID: {}, Approved by: {}", documentId, approver.getUsername());

        return new DocumentResponseDto(approved);
    }

    @Override
    @Transactional
    public DocumentResponseDto rejectDocument(Long documentId) {
        Document document = documentRepository.findById(documentId)
                .orElseThrow(() -> new RuntimeException("Document not found with ID: " + documentId));

        document.setIsApproved(false);
        document.setApprovedBy(null);

        Document rejected = documentRepository.save(document);
        log.info("Document rejected. ID: {}", documentId);

        return new DocumentResponseDto(rejected);
    }

    // Get only approved documents (for public access)
    public List<DocumentResponseDto> getApprovedDocuments() {
        List<Document> documents = documentRepository.findByIsApprovedTrue();
        return documents.stream()
                .map(DocumentResponseDto::new)
                .collect(Collectors.toList());
    }

    // Search documents by title
    public List<DocumentResponseDto> searchDocuments(String keyword) {
        List<Document> documents = documentRepository.findByTitleContainingIgnoreCase(keyword);
        return documents.stream()
                .map(DocumentResponseDto::new)
                .collect(Collectors.toList());
    }

    // Simple file type detection
    private Document.FileType getFileType(String filename) {
        if (filename == null) return Document.FileType.PDF;

        String extension = filename.toLowerCase();
        if (extension.endsWith(".pdf")) return Document.FileType.PDF;
        if (extension.endsWith(".docx") || extension.endsWith(".doc")) return Document.FileType.WORD;
        if (extension.endsWith(".pptx") || extension.endsWith(".ppt")) return Document.FileType.POWERPOINT;

        return Document.FileType.PDF; // Default
    }
    

}
