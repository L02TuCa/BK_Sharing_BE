package app.mobile.BK_sharing.category;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface CategoryRepository extends JpaRepository<Category, Long> {

    // Find by category name (exact match)
    Optional<Category> findByCategoryName(String categoryName);

    // Find by category name (case-insensitive)
    Optional<Category> findByCategoryNameIgnoreCase(String categoryName);

    // Check if category name exists
    boolean existsByCategoryName(String categoryName);

    // Check if category name exists (excluding a specific category)
    boolean existsByCategoryNameAndCategoryIdNot(String categoryName, Long categoryId);

    // Search categories by name (contains)
    List<Category> findByCategoryNameContainingIgnoreCase(String keyword);

    // Find categories created by a specific user
    List<Category> findByCreatedByUserId(Long userId);

    // Get categories with document count (custom query)
    @Query("SELECT c, COUNT(d) as documentCount FROM Category c " +
            "LEFT JOIN Document d ON c.categoryId = d.category.categoryId " +
            "GROUP BY c.categoryId")
    List<Object[]> findAllWithDocumentCount();

    // Get category with document count by ID
    @Query("SELECT c, COUNT(d) as documentCount FROM Category c " +
            "LEFT JOIN Document d ON c.categoryId = d.category.categoryId " +
            "WHERE c.categoryId = :categoryId " +
            "GROUP BY c.categoryId")
    Optional<Object[]> findByIdWithDocumentCount(@Param("categoryId") Integer categoryId);
}