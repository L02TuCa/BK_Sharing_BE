package app.mobile.BK_sharing.document;

import app.mobile.BK_sharing.document.dto.DocumentResponseDto;
import app.mobile.BK_sharing.document.dto.DocumentUploadRequest;
import app.mobile.BK_sharing.document.entity.Document;
import app.mobile.BK_sharing.document.repository.DocumentRepository;
import app.mobile.BK_sharing.dto.ApiResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

@RestController
@RequestMapping("/api/v1/documents")
@RequiredArgsConstructor
public class DocumentController {
    private final DocumentServiceImpl documentService;
    private final DocumentRepository documentRepository;

    @PostMapping(consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<ApiResponse<DocumentResponseDto>> uploadDocument(
            @ModelAttribute DocumentUploadRequest request) {

        DocumentResponseDto doc = documentService.uploadDocument(
                request.getFile(),
                request.getTitle(),
                request.getDescription(),
                request.getUserId(),
                request.getCategoryIds(),
                request.getCourseId()
        );
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(ApiResponse.success("Document uploaded successfully", doc));
    }

    @GetMapping
    public ResponseEntity<ApiResponse<List<DocumentResponseDto>>> getAllDocuments() {
        List<DocumentResponseDto> documents = documentService.getAllDocuments();
        return ResponseEntity.ok(ApiResponse.success("Documents retrieved successfully", documents));
    }

    @GetMapping("/paginated")
    public ResponseEntity<ApiResponse<Page<DocumentResponseDto>>> getAllDocumentsPaginated(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size,
            @RequestParam(defaultValue = "createdAt") String sortBy,
            @RequestParam(defaultValue = "DESC") String direction) {

        Sort sort = direction.equalsIgnoreCase("ASC") ?
                Sort.by(sortBy).ascending() : Sort.by(sortBy).descending();
        Pageable pageable = PageRequest.of(page, size, sort);

        Page<DocumentResponseDto> documents = documentService.getAllDocuments(pageable);
        return ResponseEntity.ok(ApiResponse.success("Documents retrieved successfully", documents));
    }

    @GetMapping("/{id}")
    public ResponseEntity<ApiResponse<DocumentResponseDto>> getDocumentById(
            @PathVariable Long id) {

        DocumentResponseDto document = documentService.getDocumentById(id);
        return ResponseEntity.ok(ApiResponse.success("Document retrieved successfully", document));
    }

    @GetMapping("/user/{userId}")
    public ResponseEntity<ApiResponse<List<DocumentResponseDto>>> getDocumentsByUser(
            @PathVariable Long userId) {

        List<DocumentResponseDto> documents = documentService.getDocumentsByUser(userId);
        return ResponseEntity.ok(ApiResponse.success("User documents retrieved successfully", documents));
    }

    @GetMapping("/approved")
    public ResponseEntity<ApiResponse<List<DocumentResponseDto>>> getApprovedDocuments() {
        List<DocumentResponseDto> documents = documentService.getApprovedDocuments();
        return ResponseEntity.ok(ApiResponse.success("Approved documents retrieved successfully", documents));
    }

    @GetMapping("/search")
    public ResponseEntity<ApiResponse<List<DocumentResponseDto>>> searchDocuments(
            @RequestParam String keyword) {

        List<DocumentResponseDto> documents = documentService.searchDocuments(keyword);
        return ResponseEntity.ok(ApiResponse.success("Search results retrieved successfully", documents));
    }

    @PutMapping("/{id}")
    public ResponseEntity<ApiResponse<DocumentResponseDto>> updateDocument(
            @PathVariable Long id,
            @RequestParam(required = false) String title,
            @RequestParam(required = false) String description) {

        DocumentResponseDto updatedDocument = documentService.updateDocument(id, title, description);
        return ResponseEntity.ok(ApiResponse.success("Document updated successfully", updatedDocument));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<ApiResponse<Void>> deleteDocument(
            @PathVariable Long id) {

        documentService.deleteDocument(id);
        return ResponseEntity.ok(ApiResponse.success("Document deleted successfully", null));
    }

    @PutMapping("/{id}/approve")
    public ResponseEntity<ApiResponse<DocumentResponseDto>> approveDocument(
            @PathVariable Long id,
            @RequestParam Long approvedByUserId) {

        DocumentResponseDto approvedDocument = documentService.approveDocument(id, approvedByUserId);
        return ResponseEntity.ok(ApiResponse.success("Document approved successfully", approvedDocument));
    }

    @PutMapping("/{id}/reject")
    public ResponseEntity<ApiResponse<DocumentResponseDto>> rejectDocument(
            @PathVariable Long id) {

        DocumentResponseDto rejectedDocument = documentService.rejectDocument(id);
        return ResponseEntity.ok(ApiResponse.success("Document rejected successfully", rejectedDocument));
    }

    // This returns the document with the public URL for download
    @GetMapping("/{id}/download-url")
    public ResponseEntity<ApiResponse<String>> getDownloadUrl(
            @PathVariable Long id) {

        DocumentResponseDto document = documentService.getDocumentById(id);
        return ResponseEntity.ok(ApiResponse.success("Download URL retrieved successfully", document.getFilePath()));
    }

    @GetMapping("/recent")
    public ResponseEntity<ApiResponse<List<DocumentResponseDto>>> getRecentDocuments(
            @RequestParam(defaultValue = "10") int limit) {

        Pageable pageable = PageRequest.of(0, limit, Sort.by("createdAt").descending());
        Page<DocumentResponseDto> documents = documentService.getAllDocuments(pageable);
        return ResponseEntity.ok(ApiResponse.success("Recent documents retrieved successfully", documents.getContent()));
    }
}