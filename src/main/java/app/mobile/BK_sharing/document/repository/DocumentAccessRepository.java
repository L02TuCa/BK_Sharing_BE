package app.mobile.BK_sharing.document.repository;

import app.mobile.BK_sharing.document.entity.DocumentAccess;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface DocumentAccessRepository extends JpaRepository<DocumentAccess, Integer> {

    List<DocumentAccess> findByDocumentDocumentId(Integer documentId);

    List<DocumentAccess> findByUserUserId(Integer userId);

    Optional<DocumentAccess> findByDocumentDocumentIdAndUserUserId(Integer documentId, Integer userId);

    boolean existsByDocumentDocumentIdAndUserUserId(Integer documentId, Integer userId);

    List<DocumentAccess> findByGrantedByUserId(Integer userId);

    void deleteByDocumentDocumentIdAndUserUserId(Integer documentId, Integer userId);
}