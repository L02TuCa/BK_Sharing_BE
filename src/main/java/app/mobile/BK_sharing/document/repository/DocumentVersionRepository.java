package app.mobile.BK_sharing.document.repository;

import app.mobile.BK_sharing.document.entity.DocumentVersion;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface DocumentVersionRepository extends JpaRepository<DocumentVersion, Long> {
    List<DocumentVersion> findByDocumentDocumentIdOrderByVersionNumberDesc(Long documentId);

    Optional<DocumentVersion> findTopByDocumentDocumentIdOrderByVersionNumberDesc(Long documentId);

    Optional<DocumentVersion> findByDocumentDocumentIdAndVersionNumber(Long documentId, Integer versionNumber);

    int countByDocumentDocumentId(Long documentId);

    @Query("SELECT MAX(dv.versionNumber) FROM DocumentVersion dv WHERE dv.document.documentId = :documentId")
    Integer findMaxVersionNumberByDocumentId(@Param("documentId") Long documentId);

    void deleteByDocumentDocumentId(Long documentId);

    @Modifying
    @Query("DELETE FROM DocumentVersion dv WHERE dv.document.documentId = :documentId")
    void deleteAllVersionsByDocumentId(@Param("documentId") Long documentId);
}