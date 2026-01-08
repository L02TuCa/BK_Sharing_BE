package app.mobile.BK_sharing.document.controller;

import app.mobile.BK_sharing.document.entity.DocumentVersion;
import app.mobile.BK_sharing.document.service.DocumentVersionService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/v1/document-versions")
@RequiredArgsConstructor
public class DocumentVersionController {

    private final DocumentVersionService documentVersionService;

    // Get all versions for a document
    @GetMapping("/document/{documentId}")
    public ResponseEntity<List<DocumentVersion>> getDocumentVersions(@PathVariable Long documentId) {
        List<DocumentVersion> versions = documentVersionService.getVersionsByDocumentId(documentId);
        return ResponseEntity.ok(versions);
    }

    // Get specific version
    @GetMapping("/{versionId}")
    public ResponseEntity<DocumentVersion> getVersionById(@PathVariable Long versionId) {
        DocumentVersion version = documentVersionService.getVersionById(versionId);
        return ResponseEntity.ok(version);
    }

    // Get latest version for a document
    @GetMapping("/document/{documentId}/latest")
    public ResponseEntity<DocumentVersion> getLatestVersion(@PathVariable Long documentId) {
        DocumentVersion latestVersion = documentVersionService.getLatestVersion(documentId);
        return ResponseEntity.ok(latestVersion);
    }

    // Get version count for a document
    @GetMapping("/document/{documentId}/count")
    public ResponseEntity<Integer> getVersionCount(@PathVariable Long documentId) {
        int count = documentVersionService.getVersionCount(documentId);
        return ResponseEntity.ok(count);
    }
}