package app.mobile.BK_sharing.category;

import app.mobile.BK_sharing.category.dto.*;
import app.mobile.BK_sharing.user.User;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.List;

public interface CategoryService {

    // Create a new category
    CategoryDTO createCategory(CreateCategoryDTO createCategoryDTO, Long createdBy);

    // Get all categories
    List<CategoryDTO> getAllCategories();

    // Get all categories with pagination
    Page<CategoryDTO> getAllCategories(Pageable pageable);

    // Get category by ID
    CategoryDTO getCategoryById(Long categoryId);

    // Get category by name
    CategoryDTO getCategoryByName(String categoryName);

    // Search categories by name
    List<CategoryDTO> searchCategories(String keyword);

    // Update category
    CategoryDTO updateCategory(Long categoryId, UpdateCategoryDTO updateCategoryDTO);

    // Delete category
    void deleteCategory(Long categoryId);

    // Check if category exists
    boolean existsById(Long categoryId);

    // Check if category name exists
    boolean existsByCategoryName(String categoryName);

    // Get categories created by a specific user
    List<CategoryDTO> getCategoriesByCreatedBy(Long createdBy);
}

