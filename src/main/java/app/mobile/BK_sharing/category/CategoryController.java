package app.mobile.BK_sharing.category.controller;

import app.mobile.BK_sharing.category.CategoryService;
import app.mobile.BK_sharing.category.dto.*;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/v1/categories")
@RequiredArgsConstructor
@Slf4j
public class CategoryController {

    private final CategoryService categoryService;

    // Create a new category
    @PostMapping
    public ResponseEntity<CategoryResponseDTO> createCategory(
            @Valid @RequestBody CreateCategoryDTO createCategoryDTO,
            @RequestParam(defaultValue = "1") Long createdBy // In real app, get from authentication
    ) {
        log.info("POST /api/v1/categories - Creating new category: {}", createCategoryDTO.getCategoryName());

        try {
            CategoryDTO categoryDTO = categoryService.createCategory(createCategoryDTO, createdBy);

            CategoryResponseDTO response = new CategoryResponseDTO();
            response.setSuccess(true);
            response.setMessage("Category created successfully");
            response.setData(categoryDTO);

            return ResponseEntity.status(HttpStatus.CREATED).body(response);
        } catch (IllegalArgumentException e) {
            log.error("Error creating category: {}", e.getMessage());

            CategoryResponseDTO response = new CategoryResponseDTO();
            response.setSuccess(false);
            response.setMessage(e.getMessage());

            return ResponseEntity.badRequest().body(response);
        } catch (Exception e) {
            log.error("Unexpected error creating category: {}", e.getMessage(), e);

            CategoryResponseDTO response = new CategoryResponseDTO();
            response.setSuccess(false);
            response.setMessage("An unexpected error occurred");

            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(response);
        }
    }

    // Get all categories
    @GetMapping
    public ResponseEntity<CategoryListResponseDTO> getAllCategories(
            @RequestParam(required = false) String search,
            @RequestParam(defaultValue = "1") int page,
            @RequestParam(defaultValue = "20") int limit,
            @RequestParam(defaultValue = "categoryName") String sortBy,
            @RequestParam(defaultValue = "asc") String sortDirection
    ) {
        log.info("GET /api/v1/categories - Getting all categories, page: {}, limit: {}", page, limit);

        try {
            // Create pageable object
            Sort sort = sortDirection.equalsIgnoreCase("desc")
                    ? Sort.by(sortBy).descending()
                    : Sort.by(sortBy).ascending();
            Pageable pageable = PageRequest.of(page - 1, limit, sort);

            Page<CategoryDTO> categoriesPage;

            if (search != null && !search.trim().isEmpty()) {
                // If search parameter is provided, search categories
                List<CategoryDTO> categories = categoryService.searchCategories(search);

                // Manually create a page from the list
                int start = (int) pageable.getOffset();
                int end = Math.min((start + pageable.getPageSize()), categories.size());

                if (start > categories.size()) {
                    categoriesPage = Page.empty(pageable);
                } else {
                    categoriesPage = new org.springframework.data.domain.PageImpl<>(
                            categories.subList(start, end),
                            pageable,
                            categories.size()
                    );
                }
            } else {
                // Otherwise, get all categories with pagination
                categoriesPage = categoryService.getAllCategories(pageable);
            }

            CategoryListResponseDTO response = new CategoryListResponseDTO();
            response.setSuccess(true);
            response.setMessage("Categories retrieved successfully");
            response.setData(categoriesPage.getContent());

            // Set pagination info
            CategoryListResponseDTO.PaginationInfo paginationInfo =
                    new CategoryListResponseDTO.PaginationInfo();
            paginationInfo.setTotal((int) categoriesPage.getTotalElements());
            paginationInfo.setPage(page);
            paginationInfo.setLimit(limit);
            paginationInfo.setTotalPages(categoriesPage.getTotalPages());
            response.setPagination(paginationInfo);

            return ResponseEntity.ok(response);
        } catch (Exception e) {
            log.error("Error getting categories: {}", e.getMessage(), e);

            CategoryListResponseDTO response = new CategoryListResponseDTO();
            response.setSuccess(false);
            response.setMessage("An error occurred while retrieving categories");

            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(response);
        }
    }

    // Get category by ID
    @GetMapping("/{categoryId}")
    public ResponseEntity<CategoryResponseDTO> getCategoryById(@PathVariable Long categoryId) {
        log.info("GET /api/v1/categories/{} - Getting category by ID", categoryId);

        try {
            CategoryDTO categoryDTO = categoryService.getCategoryById(categoryId);

            CategoryResponseDTO response = new CategoryResponseDTO();
            response.setSuccess(true);
            response.setMessage("Category retrieved successfully");
            response.setData(categoryDTO);

            return ResponseEntity.ok(response);
        } catch (RuntimeException e) {
            log.error("Error getting category: {}", e.getMessage());

            CategoryResponseDTO response = new CategoryResponseDTO();
            response.setSuccess(false);
            response.setMessage(e.getMessage());

            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(response);
        } catch (Exception e) {
            log.error("Unexpected error getting category: {}", e.getMessage(), e);

            CategoryResponseDTO response = new CategoryResponseDTO();
            response.setSuccess(false);
            response.setMessage("An unexpected error occurred");

            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(response);
        }
    }

    // Get category by name
    @GetMapping("/name/{categoryName}")
    public ResponseEntity<CategoryResponseDTO> getCategoryByName(@PathVariable String categoryName) {
        log.info("GET /api/v1/categories/name/{} - Getting category by name", categoryName);

        try {
            CategoryDTO categoryDTO = categoryService.getCategoryByName(categoryName);

            CategoryResponseDTO response = new CategoryResponseDTO();
            response.setSuccess(true);
            response.setMessage("Category retrieved successfully");
            response.setData(categoryDTO);

            return ResponseEntity.ok(response);
        } catch (RuntimeException e) {
            log.error("Error getting category: {}", e.getMessage());

            CategoryResponseDTO response = new CategoryResponseDTO();
            response.setSuccess(false);
            response.setMessage(e.getMessage());

            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(response);
        } catch (Exception e) {
            log.error("Unexpected error getting category: {}", e.getMessage(), e);

            CategoryResponseDTO response = new CategoryResponseDTO();
            response.setSuccess(false);
            response.setMessage("An unexpected error occurred");

            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(response);
        }
    }

    // Update category
    @PutMapping("/{categoryId}")
    public ResponseEntity<CategoryResponseDTO> updateCategory(
            @PathVariable Long categoryId,
            @Valid @RequestBody UpdateCategoryDTO updateCategoryDTO
    ) {
        log.info("PUT /api/v1/categories/{} - Updating category", categoryId);

        try {
            CategoryDTO categoryDTO = categoryService.updateCategory(categoryId, updateCategoryDTO);

            CategoryResponseDTO response = new CategoryResponseDTO();
            response.setSuccess(true);
            response.setMessage("Category updated successfully");
            response.setData(categoryDTO);

            return ResponseEntity.ok(response);
        } catch (RuntimeException e) {
            log.error("Error updating category: {}", e.getMessage());

            CategoryResponseDTO response = new CategoryResponseDTO();
            response.setSuccess(false);
            response.setMessage(e.getMessage());

            if (e.getMessage().contains("not found")) {
                return ResponseEntity.status(HttpStatus.NOT_FOUND).body(response);
            } else {
                return ResponseEntity.badRequest().body(response);
            }
        } catch (Exception e) {
            log.error("Unexpected error updating category: {}", e.getMessage(), e);

            CategoryResponseDTO response = new CategoryResponseDTO();
            response.setSuccess(false);
            response.setMessage("An unexpected error occurred");

            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(response);
        }
    }

    // Delete category
    @DeleteMapping("/{categoryId}")
    public ResponseEntity<CategoryResponseDTO> deleteCategory(@PathVariable Long categoryId) {
        log.info("DELETE /api/v1/categories/{} - Deleting category", categoryId);

        try {
            categoryService.deleteCategory(categoryId);

            CategoryResponseDTO response = new CategoryResponseDTO();
            response.setSuccess(true);
            response.setMessage("Category deleted successfully");

            return ResponseEntity.ok(response);
        } catch (RuntimeException e) {
            log.error("Error deleting category: {}", e.getMessage());

            CategoryResponseDTO response = new CategoryResponseDTO();
            response.setSuccess(false);
            response.setMessage(e.getMessage());

            if (e.getMessage().contains("not found")) {
                return ResponseEntity.status(HttpStatus.NOT_FOUND).body(response);
            } else {
                return ResponseEntity.badRequest().body(response);
            }
        } catch (Exception e) {
            log.error("Unexpected error deleting category: {}", e.getMessage(), e);

            CategoryResponseDTO response = new CategoryResponseDTO();
            response.setSuccess(false);
            response.setMessage("An unexpected error occurred");

            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(response);
        }
    }

    // Check if category exists by name
    @GetMapping("/exists/{categoryName}")
    public ResponseEntity<CategoryResponseDTO> checkCategoryExists(@PathVariable String categoryName) {
        log.info("GET /api/v1/categories/exists/{} - Checking if category exists", categoryName);

        try {
            boolean exists = categoryService.existsByCategoryName(categoryName);

            CategoryResponseDTO response = new CategoryResponseDTO();
            response.setSuccess(true);
            response.setMessage(exists ? "Category exists" : "Category does not exist");

            // You can also return the category data if it exists
            if (exists) {
                try {
                    CategoryDTO categoryDTO = categoryService.getCategoryByName(categoryName);
                    response.setData(categoryDTO);
                } catch (Exception e) {
                    // Category exists but couldn't fetch details
                    response.setData(null);
                }
            }

            return ResponseEntity.ok(response);
        } catch (Exception e) {
            log.error("Error checking category existence: {}", e.getMessage(), e);

            CategoryResponseDTO response = new CategoryResponseDTO();
            response.setSuccess(false);
            response.setMessage("An error occurred while checking category existence");

            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(response);
        }
    }

    // Get categories created by a specific user
    @GetMapping("/user/{userId}")
    public ResponseEntity<CategoryListResponseDTO> getCategoriesByUser(@PathVariable Long userId) {
        log.info("GET /api/v1/categories/user/{} - Getting categories by user", userId);

        try {
            List<CategoryDTO> categories = categoryService.getCategoriesByCreatedBy(userId);

            CategoryListResponseDTO response = new CategoryListResponseDTO();
            response.setSuccess(true);
            response.setMessage("Categories retrieved successfully");
            response.setData(categories);

            // Set pagination info for consistency (all items in one page)
            CategoryListResponseDTO.PaginationInfo paginationInfo =
                    new CategoryListResponseDTO.PaginationInfo();
            paginationInfo.setTotal(categories.size());
            paginationInfo.setPage(1);
            paginationInfo.setLimit(categories.size());
            paginationInfo.setTotalPages(1);
            response.setPagination(paginationInfo);

            return ResponseEntity.ok(response);
        } catch (Exception e) {
            log.error("Error getting categories by user: {}", e.getMessage(), e);

            CategoryListResponseDTO response = new CategoryListResponseDTO();
            response.setSuccess(false);
            response.setMessage("An error occurred while retrieving categories");

            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(response);
        }
    }
}