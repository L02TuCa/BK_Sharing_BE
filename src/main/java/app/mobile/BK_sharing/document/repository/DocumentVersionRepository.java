package app.mobile.BK_sharing.document.repository;

import app.mobile.BK_sharing.document.entity.DocumentVersion;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface DocumentVersionRepository extends JpaRepository<DocumentVersion, Integer> {

    List<DocumentVersion> findByDocumentDocumentId(Integer documentId);

    DocumentVersion findByDocumentDocumentIdAndVersionNumber(Integer documentId, Integer versionNumber);

    Integer countByDocumentDocumentId(Integer documentId);

    DocumentVersion findTopByDocumentDocumentIdOrderByVersionNumberDesc(Integer documentId);

    List<DocumentVersion> findByEditedByUserId(Integer userId);
}