package app.mobile.BK_sharing.document.repository;

import app.mobile.BK_sharing.document.entity.DocumentInteraction;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;

@Repository
public interface DocumentInteractionRepository extends JpaRepository<DocumentInteraction, Integer> {

    List<DocumentInteraction> findByDocumentDocumentId(Integer documentId);

    List<DocumentInteraction> findByUserUserId(Integer userId);

    Long countByDocumentDocumentId(Integer documentId);

    Long countByDocumentDocumentIdAndInteractionType(Integer documentId, DocumentInteraction.InteractionType type);

    Long countByUserUserIdAndInteractionType(Integer userId, DocumentInteraction.InteractionType type);

    List<DocumentInteraction> findByInteractionAtBetween(LocalDateTime start, LocalDateTime end);

    List<DocumentInteraction> findByDocumentDocumentIdAndInteractionType(Integer documentId, DocumentInteraction.InteractionType type);

    List<DocumentInteraction> findByUserUserIdAndInteractionType(Integer userId, DocumentInteraction.InteractionType type);
}