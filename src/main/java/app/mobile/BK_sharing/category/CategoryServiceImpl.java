package app.mobile.BK_sharing.category;

import app.mobile.BK_sharing.category.dto.CategoryDTO;
import app.mobile.BK_sharing.category.dto.CreateCategoryDTO;
import app.mobile.BK_sharing.category.dto.UpdateCategoryDTO;
import app.mobile.BK_sharing.user.User;
import app.mobile.BK_sharing.user.UserRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Slf4j
public class CategoryServiceImpl implements CategoryService {

    private final CategoryRepository categoryRepository;
    private final UserRepository userRepository;

    @Override
    @Transactional
    public CategoryDTO createCategory(CreateCategoryDTO createCategoryDTO, Long createdByUserId) {
        log.info("Creating new category: {} by user: {}", createCategoryDTO.getCategoryName(), createdByUserId);

        // Check if category name already exists
        if (categoryRepository.existsByCategoryName(createCategoryDTO.getCategoryName())) {
            throw new IllegalArgumentException("Category name already exists: " + createCategoryDTO.getCategoryName());
        }

        // Find the user who created this category
        User createdByUser = userRepository.findById(createdByUserId)
                .orElseThrow(() -> new RuntimeException("User not found with ID: " + createdByUserId));

        // Create new category entity
        Category category = new Category();
        category.setCategoryName(createCategoryDTO.getCategoryName());
        category.setDescription(createCategoryDTO.getDescription());
        category.setCreatedBy(createdByUser);

        // Save to database
        Category savedCategory = categoryRepository.save(category);
        log.info("Category created successfully with ID: {}", savedCategory.getCategoryId());

        return convertToDTO(savedCategory);
    }

    @Override
    @Transactional(readOnly = true)
    public List<CategoryDTO> getAllCategories() {
        log.info("Fetching all categories");
        return categoryRepository.findAll().stream()
                .map(this::convertToDTO)
                .collect(Collectors.toList());
    }

    @Override
    @Transactional(readOnly = true)
    public Page<CategoryDTO> getAllCategories(Pageable pageable) {
        log.info("Fetching all categories with pagination: {}", pageable);
        return categoryRepository.findAll(pageable)
                .map(this::convertToDTO);
    }

    @Override
    @Transactional(readOnly = true)
    public CategoryDTO getCategoryById(Long categoryId) {
        log.info("Fetching category by ID: {}", categoryId);
        Category category = categoryRepository.findById(categoryId)
                .orElseThrow(() -> new RuntimeException("Category not found with ID: " + categoryId));

        return convertToDTO(category);
    }

    @Override
    @Transactional(readOnly = true)
    public CategoryDTO getCategoryByName(String categoryName) {
        log.info("Fetching category by name: {}", categoryName);
        Category category = categoryRepository.findByCategoryName(categoryName)
                .orElseThrow(() -> new RuntimeException("Category not found with name: " + categoryName));

        return convertToDTO(category);
    }

    @Override
    @Transactional(readOnly = true)
    public List<CategoryDTO> searchCategories(String keyword) {
        log.info("Searching categories with keyword: {}", keyword);
        if (keyword == null || keyword.trim().isEmpty()) {
            return getAllCategories();
        }

        return categoryRepository.findByCategoryNameContainingIgnoreCase(keyword).stream()
                .map(this::convertToDTO)
                .collect(Collectors.toList());
    }

    @Override
    @Transactional
    public CategoryDTO updateCategory(Long categoryId, UpdateCategoryDTO updateCategoryDTO) {
        log.info("Updating category with ID: {}", categoryId);

        // Find existing category
        Category category = categoryRepository.findById(categoryId)
                .orElseThrow(() -> new RuntimeException("Category not found with ID: " + categoryId));

        // Check if new category name conflicts with existing categories
        if (updateCategoryDTO.getCategoryName() != null &&
                !updateCategoryDTO.getCategoryName().equals(category.getCategoryName())) {

            if (categoryRepository.existsByCategoryNameAndCategoryIdNot(
                    updateCategoryDTO.getCategoryName(), categoryId)) {
                throw new IllegalArgumentException("Category name already exists: " + updateCategoryDTO.getCategoryName());
            }

            category.setCategoryName(updateCategoryDTO.getCategoryName());
        }

        // Update description if provided
        if (updateCategoryDTO.getDescription() != null) {
            category.setDescription(updateCategoryDTO.getDescription());
        }

        // Save updated category
        Category updatedCategory = categoryRepository.save(category);
        log.info("Category updated successfully with ID: {}", categoryId);

        return convertToDTO(updatedCategory);
    }

    @Override
    @Transactional
    public void deleteCategory(Long categoryId) {
        log.info("Deleting category with ID: {}", categoryId);

        // Check if category exists
        if (!categoryRepository.existsById(categoryId)) {
            throw new RuntimeException("Category not found with ID: " + categoryId);
        }

        // Optional: Check if category has associated documents
        // This would require a DocumentRepository dependency
        // List<Document> documents = documentRepository.findByCategoryId(categoryId);
        // if (!documents.isEmpty()) {
        //     throw new RuntimeException("Cannot delete category with associated documents");
        // }

        // Delete category
        categoryRepository.deleteById(categoryId);
        log.info("Category deleted successfully with ID: {}", categoryId);
    }

    @Override
    @Transactional(readOnly = true)
    public boolean existsById(Long categoryId) {
        return categoryRepository.existsById(categoryId);
    }

    @Override
    @Transactional(readOnly = true)
    public boolean existsByCategoryName(String categoryName) {
        return categoryRepository.existsByCategoryName(categoryName);
    }

    @Override
    @Transactional(readOnly = true)
    public List<CategoryDTO> getCategoriesByCreatedBy(Long createdBy) {
        log.info("Fetching categories created by user: {}", createdBy);
        return categoryRepository.findByCreatedByUserId(createdBy).stream()
                .map(this::convertToDTO)
                .collect(Collectors.toList());
    }

    // Helper method to convert Entity to DTO
    private CategoryDTO convertToDTO(Category category) {
        CategoryDTO dto = new CategoryDTO();
        dto.setCategoryId(category.getCategoryId());
        dto.setCategoryName(category.getCategoryName());
        dto.setDescription(category.getDescription());
        dto.setCreatedBy(category.getCreatedBy().getUserId());
        dto.setCreatedAt(category.getCreatedAt());

        // Optional: Set document count (would need additional query)
        // dto.setDocumentCount(documentCount);

        return dto;
    }
}