package app.mobile.BK_sharing.document;

import app.mobile.BK_sharing.category.Category;
import app.mobile.BK_sharing.category.CategoryRepository;
import app.mobile.BK_sharing.course.Course;
import app.mobile.BK_sharing.course.CourseRepository;
import app.mobile.BK_sharing.document.dto.DocumentResponseDto;
import app.mobile.BK_sharing.document.entity.Document;
import app.mobile.BK_sharing.document.repository.DocumentRepository;
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

    @Override
    @Transactional
    public DocumentResponseDto uploadDocument(
            MultipartFile file,
            String title,
            String description,
            Long userId,
            List<Long> categoryIds,  // Multiple categories
            Long courseId) {         // Single course

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
//                case "xlsx", "xls" -> doc.setFileType(Document.FileType.EXCEL);
//                case "txt" -> doc.setFileType(Document.FileType.TEXT);
//                case "jpg", "jpeg", "png", "gif" -> doc.setFileType(Document.FileType.IMAGE);
                default -> doc.setFileType(Document.FileType.OTHER);
            }

            // 5. Set uploader
            User uploader = userRepository.findById(userId)
                    .orElseThrow(() -> new RuntimeException("User not found with ID: " + userId));
            doc.setUploadedBy(uploader);

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

                // Update reverse side if needed (optional, depends on cascade)
                categories.forEach(category -> {
                    if (category.getDocuments() == null) {
                        category.setDocuments(new ArrayList<>());
                    }
                    category.getDocuments().add(doc);
                });
            }

            // 9. Handle course (ManyToOne)
            if (courseId != null) {
                Course course = courseRepository.findById(courseId)
                        .orElseThrow(() -> new RuntimeException("Course not found with ID: " + courseId));
                doc.setCourse(course);

                // Add to reverse side if needed
                if (course.getDocuments() == null) {
                    course.setDocuments(new ArrayList<>());
                }
                course.getDocuments().add(doc);
            }

            // 10. Save with associations
            Document savedDocument = documentRepository.save(doc);

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

    @Override
    @Transactional
    public DocumentResponseDto updateDocument(Long id, String title, String description) {
        Document document = documentRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Document not found with ID: " + id));

        if (title != null && !title.trim().isEmpty()) {
            document.setTitle(title);
        }

        if (description != null) {
            document.setDescription(description);
        }

        Document updated = documentRepository.save(document);
        log.info("Document updated. ID: {}", id);

        return new DocumentResponseDto(updated);
    }

    @Override
    @Transactional
    public void deleteDocument(Long id) {
        Document document = documentRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Document not found with ID: " + id));

        // Delete file from Supabase storage
        try {
            storageService.deleteFile(document.getFilePath());
            log.info("File deleted from Supabase: {}", document.getFilePath());
        } catch (IOException e) {
            log.warn("Failed to delete file from Supabase, but continuing with DB delete: {}", e.getMessage());
            // Continue with DB deletion even if Supabase delete fails
        }

        documentRepository.delete(document);
        log.info("Document deleted from database. ID: {}", id);
    }

    @Override
    public List<DocumentResponseDto> getDocumentsByUser(Long userId) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new RuntimeException("User not found with ID: " + userId));
        List<Document> documents = documentRepository.findByApprovedByUserId(user.getUserId());
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
