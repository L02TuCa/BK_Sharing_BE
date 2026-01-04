package app.mobile.BK_sharing.document;

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

    @Override
    @Transactional
    public DocumentResponseDto uploadDocument(MultipartFile file, String title, String description, Long userId) {
        try {
            // 1. Basic validation
            if (file.isEmpty()) {
                throw new RuntimeException("File is empty");
            }

            // 2. Upload to Supabase and get PUBLIC URL
            String publicUrl = storageService.uploadFile(file);

            Document doc = new Document();
            doc.setTitle(title);
            doc.setDescription(description);
            doc.setFilePath(publicUrl);
            doc.setFileSize(file.getSize());

            // Fix: Don't redeclare 'extension' variable
            String originalFilename = file.getOriginalFilename();
            assert originalFilename != null;
            String fileExtension = originalFilename.substring(originalFilename.lastIndexOf(".") + 1).toLowerCase();

            switch (fileExtension) {
                case "pdf" -> doc.setFileType(Document.FileType.PDF);
                case "docx", "doc" -> doc.setFileType(Document.FileType.Word);
                case "pptx", "ppt" -> doc.setFileType(Document.FileType.PowerPoint);
                default -> doc.setFileType(Document.FileType.PDF); // Default
            }

            // Set uploader (make sure user exists)
            User uploader = userRepository.findById(userId)
                    .orElseThrow(() -> new RuntimeException("User not found"));
            doc.setUploadedBy(uploader);

            // 5. Save to database
            documentRepository.save(doc);

            return new DocumentResponseDto(doc);

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
        if (extension.endsWith(".docx") || extension.endsWith(".doc")) return Document.FileType.Word;
        if (extension.endsWith(".pptx") || extension.endsWith(".ppt")) return Document.FileType.PowerPoint;

        return Document.FileType.PDF; // Default
    }
    

}
