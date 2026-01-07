package app.mobile.BK_sharing.document.repository;

import app.mobile.BK_sharing.category.Category;
import app.mobile.BK_sharing.document.entity.Document;
import app.mobile.BK_sharing.user.User;
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

    // CORRECTED: Find documents by title (contains, case-insensitive)
    List<Document> findByTitleContainingIgnoreCase(String title);

    // CORRECTED: Find documents by category (using JOIN query)
    @Query("SELECT DISTINCT d FROM Document d JOIN d.categories c WHERE c.categoryId = :categoryId")
    List<Document> findByCategoryId(@Param("categoryId") Long categoryId);

    // CORRECTED: Find documents by category with pagination
    @Query("SELECT DISTINCT d FROM Document d JOIN d.categories c WHERE c.categoryId = :categoryId")
    Page<Document> findByCategoryId(@Param("categoryId") Long categoryId, Pageable pageable);

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

    // CORRECTED: Find documents by category and approval status
    @Query("SELECT DISTINCT d FROM Document d JOIN d.categories c WHERE c.categoryId = :categoryId AND d.isApproved = :isApproved")
    List<Document> findByCategoryIdAndIsApproved(@Param("categoryId") Long categoryId, @Param("isApproved") Boolean isApproved);

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

    // CORRECTED: Count documents by category
    @Query("SELECT COUNT(DISTINCT d) FROM Document d JOIN d.categories c WHERE c.categoryId = :categoryId")
    Long countByCategoryId(@Param("categoryId") Long categoryId);

    // Count documents by uploader
    Long countByUploadedByUserId(Long userId);

    // Count documents by approval status
    Long countByIsApproved(Boolean isApproved);

    // CORRECTED: Find documents with pagination and optional filters
    @Query("SELECT DISTINCT d FROM Document d " +
            "LEFT JOIN d.categories c " +
            "WHERE " +
            "(:title IS NULL OR LOWER(d.title) LIKE LOWER(CONCAT('%', :title, '%'))) AND " +
            "(:categoryId IS NULL OR c.categoryId = :categoryId) AND " +
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

    // CORRECTED: Find documents uploaded by user with category filter
    @Query("SELECT DISTINCT d FROM Document d JOIN d.categories c " +
            "WHERE d.uploadedBy.userId = :userId AND " +
            "(:categoryId IS NULL OR c.categoryId = :categoryId)")
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

    // CORRECTED: Get document statistics by category
    @Query("SELECT c.categoryName, COUNT(DISTINCT d) as documentCount " +
            "FROM Category c LEFT JOIN c.documents d " +
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

    // CORRECTED: Find documents by category and uploader
    @Query("SELECT DISTINCT d FROM Document d JOIN d.categories c " +
            "WHERE c.categoryId = :categoryId AND d.uploadedBy.userId = :userId")
    List<Document> findByCategoryIdAndUploadedByUserId(
            @Param("categoryId") Long categoryId,
            @Param("userId") Long userId
    );

    // Search by title or description
    @Query("SELECT d FROM Document d WHERE " +
            "LOWER(d.title) LIKE LOWER(CONCAT('%', :keyword, '%')) OR " +
            "LOWER(d.description) LIKE LOWER(CONCAT('%', :keyword, '%'))")
    List<Document> searchDocuments(@Param("keyword") String keyword);

    // CORRECTED: Find documents by categories (multiple categories)
    @Query("SELECT DISTINCT d FROM Document d JOIN d.categories c WHERE c.categoryId IN :categoryIds")
    List<Document> findByCategoryIds(@Param("categoryIds") List<Long> categoryIds);

    // Find documents by course
    List<Document> findByCourseCourseId(Long courseId);

    // Find documents by course with pagination
    Page<Document> findByCourseCourseId(Long courseId, Pageable pageable);
}