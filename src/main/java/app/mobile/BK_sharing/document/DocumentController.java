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

//    @GetMapping("/paginated")
//    public ResponseEntity<ApiResponse<Page<DocumentResponseDto>>> getAllDocumentsPaginated(
//            @RequestParam(defaultValue = "0") int page,
//            @RequestParam(defaultValue = "10") int size,
//            @RequestParam(defaultValue = "createdAt") String sortBy,
//            @RequestParam(defaultValue = "DESC") String direction) {
//
//        Sort sort = direction.equalsIgnoreCase("ASC") ?
//                Sort.by(sortBy).ascending() : Sort.by(sortBy).descending();
//        Pageable pageable = PageRequest.of(page, size, sort);
//
//        Page<DocumentResponseDto> documents = documentService.getAllDocuments(pageable);
//        return ResponseEntity.ok(ApiResponse.success("Documents retrieved successfully", documents));
//    }

    @GetMapping("/{id}")
    public ResponseEntity<ApiResponse<DocumentResponseDto>> getDocumentById(
            @PathVariable Long id) {

        DocumentResponseDto document = documentService.getDocumentById(id);
        return ResponseEntity.ok(ApiResponse.success("Document retrieved successfully", document));
    }

    @PutMapping("/{id}")
    public ResponseEntity<ApiResponse<DocumentResponseDto>> updateDocument(
            @PathVariable Long id,
            @RequestParam(value = "file", required = false) MultipartFile file,
            @RequestParam(value = "title", required = false) String title,
            @RequestParam(value = "description", required = false) String description,
            @RequestParam(value = "changeDescription", required = false) String changeDescription,
            @RequestParam(value = "userId") Long userId,
            @RequestParam(value = "categoryIds", required = false) List<Long> categoryIds,
            @RequestParam(value = "courseId", required = false) Long courseId) {

        DocumentResponseDto updatedDocument;

        if (file != null && !file.isEmpty()) {
            // Update with new file (creates new version)
            updatedDocument = documentService.updateDocumentWithFile(
                    id, file, changeDescription, userId);
        } else {
            // Update metadata only (no new version)
            updatedDocument = documentService.updateDocumentMetadata(
                    id, title, description, categoryIds, courseId);
        }

        return ResponseEntity.ok(ApiResponse.success("Document updated successfully", updatedDocument));
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