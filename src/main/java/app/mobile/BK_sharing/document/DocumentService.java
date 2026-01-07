package app.mobile.BK_sharing.document;

import app.mobile.BK_sharing.document.dto.DocumentResponseDto;
import app.mobile.BK_sharing.document.entity.Document;
import app.mobile.BK_sharing.user.User;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.List;

public interface DocumentService {

    DocumentResponseDto uploadDocument(MultipartFile file, String title, String description, Long userId, List<Long> categoriesId, Long courseId);
    DocumentResponseDto getDocumentById(Long id);
    List<DocumentResponseDto> getAllDocuments();
    Page<DocumentResponseDto> getAllDocuments(Pageable pageable);
    DocumentResponseDto updateDocument(Long id, String title, String description);
    void deleteDocument(Long id);
    List<DocumentResponseDto> getDocumentsByUser(Long userId);
    DocumentResponseDto approveDocument(Long documentId, Long approvedByUserId);
    DocumentResponseDto rejectDocument(Long documentId);
}
