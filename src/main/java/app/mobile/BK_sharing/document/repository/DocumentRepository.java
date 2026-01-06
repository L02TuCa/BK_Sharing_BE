package app.mobile.BK_sharing.document.repository;

import app.mobile.BK_sharing.document.entity.Document;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Repository
public interface DocumentRepository extends JpaRepository<Document, Long> {

    // Find documents by title (contains, case-insensitive)
    List<Document> findByTitleContainingIgnoreCase(String title);

    // Find documents by category
    List<Document> findByCategoryCategoryId(Long categoryId);

    // Find documents by category with pagination
    Page<Document> findByCategoryCategoryId(Long categoryId, Pageable pageable);

    // Find documents by uploader
    List<Document> findByUploadedByUserId(Long userId);

    // Find documents by uploader with pagination
    Page<Document> findByUploadedByUserId(Long userId, Pageable pageable);

    // Find documents by approval status
    List<Document> findByIsApproved(Boolean isApproved);

    // Find documents by approval status with pagination
    Page<Document> findByIsApproved(Boolean isApproved, Pageable pageable);

    // Find documents by uploader and approval status
    List<Document> findByUploadedByUserIdAndIsApproved(Long userId, Boolean isApproved);

    // Find documents by category and approval status
    List<Document> findByCategoryCategoryIdAndIsApproved(Long categoryId, Boolean isApproved);

    // Find documents by approver
    List<Document> findByApprovedByUserId(Long userId);

    // Find documents by file type
    List<Document> findByFileType(Document.FileType fileType);

    // Find approved documents
    List<Document> findByIsApprovedTrue();

    Page<Document> findByIsApprovedTrue(Pageable pageable);

    // Find pending documents (not approved)
    List<Document> findByIsApprovedFalse();

    // Search documents by title or description
    List<Document> findByTitleContainingIgnoreCaseOrDescriptionContainingIgnoreCase(String titleKeyword, String descriptionKeyword);

    // Count documents by category
    Long countByCategoryCategoryId(Long categoryId);

    // Count documents by uploader
    Long countByUploadedByUserId(Long userId);

    // Count documents by approval status
    Long countByIsApproved(Boolean isApproved);

    // Find documents with pagination and optional filters
    @Query("SELECT d FROM Document d WHERE " +
            "(:title IS NULL OR LOWER(d.title) LIKE LOWER(CONCAT('%', :title, '%'))) AND " +
            "(:categoryId IS NULL OR d.category.categoryId = :categoryId) AND " +
            "(:uploadedById IS NULL OR d.uploadedBy.userId = :uploadedById) AND " +
            "(:isApproved IS NULL OR d.isApproved = :isApproved) AND " +
            "(:fileType IS NULL OR d.fileType = :fileType)")
    Page<Document> findDocumentsByFilters(
            @Param("title") String title,
            @Param("categoryId") Long categoryId,
            @Param("uploadedById") Long uploadedById,
            @Param("isApproved") Boolean isApproved,
            @Param("fileType") Document.FileType fileType,
            Pageable pageable
    );

    // Find recent documents
    @Query("SELECT d FROM Document d ORDER BY d.createdAt DESC")
    List<Document> findRecentDocuments(Pageable pageable);

    // Find documents uploaded by user with category filter
    @Query("SELECT d FROM Document d WHERE d.uploadedBy.userId = :userId AND " +
            "(:categoryId IS NULL OR d.category.categoryId = :categoryId)")
    List<Document> findByUploadedByUserIdAndCategoryId(
            @Param("userId") Long userId,
            @Param("categoryId") Long categoryId
    );

    // Get document statistics by user
    @Query("SELECT COUNT(d) as totalDocuments, " +
            "SUM(CASE WHEN d.isApproved = true THEN 1 ELSE 0 END) as approvedDocuments, " +
            "SUM(CASE WHEN d.isApproved = false THEN 1 ELSE 0 END) as pendingDocuments " +
            "FROM Document d WHERE d.uploadedBy.userId = :userId")
    Object[] getDocumentStatisticsByUser(@Param("userId") Long userId);

    // Get document statistics by category
    @Query("SELECT c.categoryName, COUNT(d) as documentCount " +
            "FROM Document d RIGHT JOIN d.category c " +
            "GROUP BY c.categoryId, c.categoryName " +
            "ORDER BY documentCount DESC")
    List<Object[]> getDocumentCountByCategory();

    // Check if document exists by title (exact match, case-insensitive)
    boolean existsByTitleIgnoreCase(String title);

    // Check if document exists by title and not the same document
    boolean existsByTitleIgnoreCaseAndDocumentIdNot(String title, Long documentId);

    // Find documents with file size greater than
    List<Document> findByFileSizeGreaterThan(Long fileSize);

    // Find documents with file size less than
    List<Document> findByFileSizeLessThan(Long fileSize);

    // Find documents by upload date range
    @Query("SELECT d FROM Document d WHERE d.createdAt BETWEEN :startDate AND :endDate")
    List<Document> findByCreatedAtBetween(
            @Param("startDate") LocalDateTime startDate,
            @Param("endDate") LocalDateTime endDate
    );

    // Find documents by uploader and approval status with pagination
    Page<Document> findByUploadedByUserIdAndIsApproved(Long userId, Boolean isApproved, Pageable pageable);

    // Find documents by category and uploader
    List<Document> findByCategoryCategoryIdAndUploadedByUserId(Long categoryId, Long userId);

    // Search by title or description
    @Query("SELECT d FROM Document d WHERE " +
            "LOWER(d.title) LIKE LOWER(CONCAT('%', :keyword, '%')) OR " +
            "LOWER(d.description) LIKE LOWER(CONCAT('%', :keyword, '%'))")
    List<Document> searchDocuments(@Param("keyword") String keyword);
}