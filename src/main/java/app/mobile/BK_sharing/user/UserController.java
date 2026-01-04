package app.mobile.BK_sharing.user;

import app.mobile.BK_sharing.dto.ApiResponse;
import app.mobile.BK_sharing.user.dto.*;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/v1/users")
@RequiredArgsConstructor
@Slf4j
public class UserController {

    private final UserService userService;

    /**
     * Create new user
     */
    @PostMapping
    public ResponseEntity<ApiResponse<UserDto>> createUser(
            @Valid @RequestBody CreateUserDto request) {
        log.info("Creating new user with username: {}", request.getUsername());

        UserDto createdUser = userService.createUser(request);


        return ResponseEntity
                .status(HttpStatus.CREATED)
                .body(ApiResponse.success("User created successfully", createdUser));
    }

    @GetMapping("/{userId}")
    public ResponseEntity<ApiResponse<UserDto>> getUserById(@PathVariable Long userId) {
        log.info("Get user request received for ID: {}", userId);

        // Mock data - replace with actual service call
        UserDto users = userService.getUserById(userId);

        return ResponseEntity.ok(ApiResponse.success("Users retrieved successfully", users));
    }

    @GetMapping
    public ResponseEntity<ApiResponse<List<UserDto>>> getAllUsers() {
        log.info("Getting all users");

        // Mock data - replace with actual service call
        List<UserDto> users = userService.getAllUsers();

        return ResponseEntity.ok(ApiResponse.success("Users retrieved successfully", users));
    }

        // Update user
    @PutMapping("/{userId}")
    public ResponseEntity<ApiResponse<UserDto>> updateUser(
            @PathVariable Long userId,
            @Valid @RequestBody UpdateUserDTO updateUserDTO) {
        log.info("PUT /api/v1/users/{} - Updating user", userId);

        UserDto updatedUser = userService.updateUser(userId, updateUserDTO);

        return ResponseEntity.ok(ApiResponse.success("User updated successfully", updatedUser));
    }


//
//    // Get all users
//    @GetMapping
//    public ResponseEntity<UserListResponseDTO> getAllUsers(
////            @RequestParam(required = false) String search,
////            @RequestParam(required = false) String role,
////            @RequestParam(required = false) Boolean active,
////            @RequestParam(defaultValue = "1") int page,
////            @RequestParam(defaultValue = "20") int limit,
////            @RequestParam(defaultValue = "createdAt") String sortBy,
////            @RequestParam(defaultValue = "desc") String sortDirection
//    ) {
//        log.info("GET /api/v1/users - Getting all users WITHOUT pagination");
//
//        try {
//            // Temporarily get all users without pagination
//            List<UserDto> users = userService.getAllUsers();
//
//            UserListResponseDTO response = new UserListResponseDTO();
//            response.setSuccess(true);
//            response.setMessage("Users retrieved successfully");
//            response.setData(users);
//
//            // No pagination info for now
//            response.setPagination(null);
//
//            return ResponseEntity.ok(response);
//        } catch (Exception e) {
//            log.error("Error getting users: {}", e.getMessage(), e);
//
//            UserListResponseDTO response = new UserListResponseDTO();
//            response.setSuccess(false);
//            response.setMessage("An error occurred while retrieving users");
//
//            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(response);
//        }
//
//
////        log.info("GET /api/v1/users - Getting all users, page: {}, limit: {}", page, limit);
////
////        try {
////            // Create pageable object
////            Sort sort = sortDirection.equalsIgnoreCase("desc")
////                    ? Sort.by(sortBy).descending()
////                    : Sort.by(sortBy).ascending();
////            Pageable pageable = PageRequest.of(page - 1, limit, sort);
////
////            Page<UserDto> usersPage;
////
////            if (search != null || role != null || active != null) {
////                // If search/filter parameters are provided, use custom search
////                // Note: You might need to adjust this based on your search implementation
////                if (search != null && !search.trim().isEmpty()) {
////                    List<UserDto> users = userService.searchUsers(search);
////
////                    // Apply role filter if provided
////                    if (role != null) {
////                        users = users.stream()
////                                .filter(u -> u.getRole().equalsIgnoreCase(role))
////                                .collect(java.util.stream.Collectors.toList());
////                    }
////
////                    // Apply active filter if provided
////                    if (active != null) {
////                        users = users.stream()
////                                .filter(u -> u.getIsActive().equals(active))
////                                .collect(java.util.stream.Collectors.toList());
////                    }
////
////                    // Manually create a page from the list
////                    int start = (int) pageable.getOffset();
////                    int end = Math.min((start + pageable.getPageSize()), users.size());
////
////                    if (start > users.size()) {
////                        usersPage = Page.empty(pageable);
////                    } else {
////                        usersPage = new org.springframework.data.domain.PageImpl<>(
////                                users.subList(start, end),
////                                pageable,
////                                users.size()
////                        );
////                    }
////                } else {
////                    // If only role or active filter, get filtered list
////                    List<UserDto> users;
////                    if (role != null) {
////                        users = userService.getUsersByRole(role);
////                    } else if (active != null) {
////                        users = active ? userService.getActiveUsers() : userService.getInactiveUsers();
////                    } else {
////                        users = userService.getAllUsers();
////                    }
////
////                    // Manually create a page from the list
////                    int start = (int) pageable.getOffset();
////                    int end = Math.min((start + pageable.getPageSize()), users.size());
////
////                    if (start > users.size()) {
////                        usersPage = Page.empty(pageable);
////                    } else {
////                        usersPage = new org.springframework.data.domain.PageImpl<>(
////                                users.subList(start, end),
////                                pageable,
////                                users.size()
////                        );
////                    }
////                }
////            } else {
////                // Otherwise, get all users with pagination
////                usersPage = userService.getAllUsers(pageable);
////            }
////
////            UserListResponseDTO response = new UserListResponseDTO();
////            response.setSuccess(true);
////            response.setMessage("Users retrieved successfully");
////            response.setData(usersPage.getContent());
////
////            // Set pagination info
////            UserListResponseDTO.PaginationInfo paginationInfo =
////                    new UserListResponseDTO.PaginationInfo();
////            paginationInfo.setTotal((int) usersPage.getTotalElements());
////            paginationInfo.setPage(page);
////            paginationInfo.setLimit(limit);
////            paginationInfo.setTotalPages(usersPage.getTotalPages());
////            response.setPagination(paginationInfo);
////
////            return ResponseEntity.ok(response);
////        } catch (Exception e) {
////            log.error("Error getting users: {}", e.getMessage(), e);
////
////            UserListResponseDTO response = new UserListResponseDTO();
////            response.setSuccess(false);
////            response.setMessage("An error occurred while retrieving users");
////
////            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(response);
////        }
//    }
//
//    // Get user by ID
//    @GetMapping("/{userId}")
//    public ResponseEntity<ApiResponse> getUserById(@PathVariable Integer userId) {
//        log.info("GET /api/v1/users/{} - Getting user by ID", userId);
//
//        try {
//            UserDto userDTO = userService.getUserById(userId);
//
//            ApiResponse response = new ApiResponse();
//            response.setSuccess(true);
//            response.setMessage("User retrieved successfully");
//            response.setData(userDTO);
//
//            return ResponseEntity.ok(response);
//        } catch (RuntimeException e) {
//            log.error("Error getting user: {}", e.getMessage());
//
//            ApiResponse response = new ApiResponse();
//            response.setSuccess(false);
//            response.setMessage(e.getMessage());
//
//            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(response);
//        } catch (Exception e) {
//            log.error("Unexpected error getting user: {}", e.getMessage(), e);
//
//            ApiResponse response = new ApiResponse();
//            response.setSuccess(false);
//            response.setMessage("An unexpected error occurred");
//
//            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(response);
//        }
//    }
//
//    // Get user by email
//    @GetMapping("/email/{email}")
//    public ResponseEntity<ApiResponse> getUserByEmail(@PathVariable String email) {
//        log.info("GET /api/v1/users/email/{} - Getting user by email", email);
//
//        try {
//            UserDto userDTO = userService.getUserByEmail(email);
//
//            ApiResponse response = new ApiResponse();
//            response.setSuccess(true);
//            response.setMessage("User retrieved successfully");
//            response.setData(userDTO);
//
//            return ResponseEntity.ok(response);
//        } catch (RuntimeException e) {
//            log.error("Error getting user: {}", e.getMessage());
//
//            ApiResponse response = new ApiResponse();
//            response.setSuccess(false);
//            response.setMessage(e.getMessage());
//
//            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(response);
//        } catch (Exception e) {
//            log.error("Unexpected error getting user: {}", e.getMessage(), e);
//
//            ApiResponse response = new ApiResponse();
//            response.setSuccess(false);
//            response.setMessage("An unexpected error occurred");
//
//            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(response);
//        }
//    }
//
//    // Get user by username
//    @GetMapping("/username/{username}")
//    public ResponseEntity<ApiResponse> getUserByUsername(@PathVariable String username) {
//        log.info("GET /api/v1/users/username/{} - Getting user by username", username);
//
//        try {
//            UserDto userDTO = userService.getUserByUsername(username);
//
//            ApiResponse response = new ApiResponse();
//            response.setSuccess(true);
//            response.setMessage("User retrieved successfully");
//            response.setData(userDTO);
//
//            return ResponseEntity.ok(response);
//        } catch (RuntimeException e) {
//            log.error("Error getting user: {}", e.getMessage());
//
//            ApiResponse response = new ApiResponse();
//            response.setSuccess(false);
//            response.setMessage(e.getMessage());
//
//            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(response);
//        } catch (Exception e) {
//            log.error("Unexpected error getting user: {}", e.getMessage(), e);
//
//            ApiResponse response = new ApiResponse();
//            response.setSuccess(false);
//            response.setMessage("An unexpected error occurred");
//
//            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(response);
//        }
//    }
//
//    // Update user
//    @PutMapping("/{userId}")
//    public ResponseEntity<ApiResponse> updateUser(
//            @PathVariable Integer userId,
//            @Valid @RequestBody UpdateUserDTO updateUserDTO
//    ) {
//        log.info("PUT /api/v1/users/{} - Updating user", userId);
//
//        try {
//            UserDto userDTO = userService.updateUser(userId, updateUserDTO);
//
//            ApiResponse response = new ApiResponse();
//            response.setSuccess(true);
//            response.setMessage("User updated successfully");
//            response.setData(userDTO);
//
//            return ResponseEntity.ok(response);
//        } catch (RuntimeException e) {
//            log.error("Error updating user: {}", e.getMessage());
//
//            ApiResponse response = new ApiResponse();
//            response.setSuccess(false);
//            response.setMessage(e.getMessage());
//
//            if (e.getMessage().contains("not found")) {
//                return ResponseEntity.status(HttpStatus.NOT_FOUND).body(response);
//            } else {
//                return ResponseEntity.badRequest().body(response);
//            }
//        } catch (Exception e) {
//            log.error("Unexpected error updating user: {}", e.getMessage(), e);
//
//            ApiResponse response = new ApiResponse();
//            response.setSuccess(false);
//            response.setMessage("An unexpected error occurred");
//
//            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(response);
//        }
//    }
//
//    // Delete user (soft delete - deactivate)
//    @DeleteMapping("/{userId}/deactivate")
//    public ResponseEntity<ApiResponse> deactivateUser(@PathVariable Integer userId) {
//        log.info("DELETE /api/v1/users/{}/deactivate - Deactivating user", userId);
//
//        try {
//            userService.deactivateUser(userId);
//
//            ApiResponse response = new ApiResponse();
//            response.setSuccess(true);
//            response.setMessage("User deactivated successfully");
//
//            return ResponseEntity.ok(response);
//        } catch (RuntimeException e) {
//            log.error("Error deactivating user: {}", e.getMessage());
//
//            ApiResponse response = new ApiResponse();
//            response.setSuccess(false);
//            response.setMessage(e.getMessage());
//
//            if (e.getMessage().contains("not found")) {
//                return ResponseEntity.status(HttpStatus.NOT_FOUND).body(response);
//            } else {
//                return ResponseEntity.badRequest().body(response);
//            }
//        } catch (Exception e) {
//            log.error("Unexpected error deactivating user: {}", e.getMessage(), e);
//
//            ApiResponse response = new ApiResponse();
//            response.setSuccess(false);
//            response.setMessage("An unexpected error occurred");
//
//            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(response);
//        }
//    }
//
//    // Delete user permanently
//    @DeleteMapping("/{userId}")
//    public ResponseEntity<ApiResponse> deleteUser(@PathVariable Integer userId) {
//        log.info("DELETE /api/v1/users/{} - Deleting user permanently", userId);
//
//        try {
//            userService.deleteUser(userId);
//
//            ApiResponse response = new ApiResponse();
//            response.setSuccess(true);
//            response.setMessage("User deleted successfully");
//
//            return ResponseEntity.ok(response);
//        } catch (RuntimeException e) {
//            log.error("Error deleting user: {}", e.getMessage());
//
//            ApiResponse response = new ApiResponse();
//            response.setSuccess(false);
//            response.setMessage(e.getMessage());
//
//            if (e.getMessage().contains("not found")) {
//                return ResponseEntity.status(HttpStatus.NOT_FOUND).body(response);
//            } else {
//                return ResponseEntity.badRequest().body(response);
//            }
//        } catch (Exception e) {
//            log.error("Unexpected error deleting user: {}", e.getMessage(), e);
//
//            ApiResponse response = new ApiResponse();
//            response.setSuccess(false);
//            response.setMessage("An unexpected error occurred");
//
//            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(response);
//        }
//    }
//
//    // Activate user
//    @PutMapping("/{userId}/activate")
//    public ResponseEntity<ApiResponse> activateUser(@PathVariable Integer userId) {
//        log.info("PUT /api/v1/users/{}/activate - Activating user", userId);
//
//        try {
//            UserDto userDTO = userService.activateUser(userId);
//
//            ApiResponse response = new ApiResponse();
//            response.setSuccess(true);
//            response.setMessage("User activated successfully");
//            response.setData(userDTO);
//
//            return ResponseEntity.ok(response);
//        } catch (RuntimeException e) {
//            log.error("Error activating user: {}", e.getMessage());
//
//            ApiResponse response = new ApiResponse();
//            response.setSuccess(false);
//            response.setMessage(e.getMessage());
//
//            if (e.getMessage().contains("not found")) {
//                return ResponseEntity.status(HttpStatus.NOT_FOUND).body(response);
//            } else {
//                return ResponseEntity.badRequest().body(response);
//            }
//        } catch (Exception e) {
//            log.error("Unexpected error activating user: {}", e.getMessage(), e);
//
//            ApiResponse response = new ApiResponse();
//            response.setSuccess(false);
//            response.setMessage("An unexpected error occurred");
//
//            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(response);
//        }
//    }
//
//    // Check if email exists
//    @GetMapping("/exists/email/{email}")
//    public ResponseEntity<ApiResponse> checkEmailExists(@PathVariable String email) {
//        log.info("GET /api/v1/users/exists/email/{} - Checking if email exists", email);
//
//        try {
//            boolean exists = userService.existsByEmail(email);
//
//            ApiResponse response = new ApiResponse();
//            response.setSuccess(true);
//            response.setMessage(exists ? "Email exists" : "Email does not exist");
//
//            // If email exists, return the user data
//            if (exists) {
//                try {
//                    UserDto userDTO = userService.getUserByEmail(email);
//                    response.setData(userDTO);
//                } catch (Exception e) {
//                    // Email exists but couldn't fetch user details
//                    response.setData(null);
//                }
//            }
//
//            return ResponseEntity.ok(response);
//        } catch (Exception e) {
//            log.error("Error checking email existence: {}", e.getMessage(), e);
//
//            ApiResponse response = new ApiResponse();
//            response.setSuccess(false);
//            response.setMessage("An error occurred while checking email existence");
//
//            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(response);
//        }
//    }
//
//    // Check if username exists
//    @GetMapping("/exists/username/{username}")
//    public ResponseEntity<ApiResponse> checkUsernameExists(@PathVariable String username) {
//        log.info("GET /api/v1/users/exists/username/{} - Checking if username exists", username);
//
//        try {
//            boolean exists = userService.existsByUsername(username);
//
//            ApiResponse response = new ApiResponse();
//            response.setSuccess(true);
//            response.setMessage(exists ? "Username exists" : "Username does not exist");
//
//            // If username exists, return the user data
//            if (exists) {
//                try {
//                    UserDto userDTO = userService.getUserByUsername(username);
//                    response.setData(userDTO);
//                } catch (Exception e) {
//                    // Username exists but couldn't fetch user details
//                    response.setData(null);
//                }
//            }
//
//            return ResponseEntity.ok(response);
//        } catch (Exception e) {
//            log.error("Error checking username existence: {}", e.getMessage(), e);
//
//            ApiResponse response = new ApiResponse();
//            response.setSuccess(false);
//            response.setMessage("An error occurred while checking username existence");
//
//            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(response);
//        }
//    }
//
//    // Get users by role
//    @GetMapping("/role/{role}")
//    public ResponseEntity<UserListResponseDTO> getUsersByRole(@PathVariable String role) {
//        log.info("GET /api/v1/users/role/{} - Getting users by role", role);
//
//        try {
//            List<UserDto> users = userService.getUsersByRole(role);
//
//            UserListResponseDTO response = new UserListResponseDTO();
//            response.setSuccess(true);
//            response.setMessage("Users retrieved successfully");
//            response.setData(users);
//
//            // Set pagination info for consistency
//            UserListResponseDTO.PaginationInfo paginationInfo =
//                    new UserListResponseDTO.PaginationInfo();
//            paginationInfo.setTotal(users.size());
//            paginationInfo.setPage(1);
//            paginationInfo.setLimit(users.size());
//            paginationInfo.setTotalPages(1);
//            response.setPagination(paginationInfo);
//
//            return ResponseEntity.ok(response);
//        } catch (IllegalArgumentException e) {
//            log.error("Invalid role specified: {}", e.getMessage());
//
//            UserListResponseDTO response = new UserListResponseDTO();
//            response.setSuccess(false);
//            response.setMessage("Invalid role. Must be 'Admin' or 'Student'");
//
//            return ResponseEntity.badRequest().body(response);
//        } catch (Exception e) {
//            log.error("Error getting users by role: {}", e.getMessage(), e);
//
//            UserListResponseDTO response = new UserListResponseDTO();
//            response.setSuccess(false);
//            response.setMessage("An error occurred while retrieving users");
//
//            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(response);
//        }
//    }
//
//    // Get active users
//    @GetMapping("/active")
//    public ResponseEntity<UserListResponseDTO> getActiveUsers() {
//        log.info("GET /api/v1/users/active - Getting active users");
//
//        try {
//            List<UserDto> users = userService.getActiveUsers();
//
//            UserListResponseDTO response = new UserListResponseDTO();
//            response.setSuccess(true);
//            response.setMessage("Active users retrieved successfully");
//            response.setData(users);
//
//            // Set pagination info
//            UserListResponseDTO.PaginationInfo paginationInfo =
//                    new UserListResponseDTO.PaginationInfo();
//            paginationInfo.setTotal(users.size());
//            paginationInfo.setPage(1);
//            paginationInfo.setLimit(users.size());
//            paginationInfo.setTotalPages(1);
//            response.setPagination(paginationInfo);
//
//            return ResponseEntity.ok(response);
//        } catch (Exception e) {
//            log.error("Error getting active users: {}", e.getMessage(), e);
//
//            UserListResponseDTO response = new UserListResponseDTO();
//            response.setSuccess(false);
//            response.setMessage("An error occurred while retrieving active users");
//
//            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(response);
//        }
//    }
//
//    // Get inactive users
//    @GetMapping("/inactive")
//    public ResponseEntity<UserListResponseDTO> getInactiveUsers() {
//        log.info("GET /api/v1/users/inactive - Getting inactive users");
//
//        try {
//            List<UserDto> users = userService.getInactiveUsers();
//
//            UserListResponseDTO response = new UserListResponseDTO();
//            response.setSuccess(true);
//            response.setMessage("Inactive users retrieved successfully");
//            response.setData(users);
//
//            // Set pagination info
//            UserListResponseDTO.PaginationInfo paginationInfo =
//                    new UserListResponseDTO.PaginationInfo();
//            paginationInfo.setTotal(users.size());
//            paginationInfo.setPage(1);
//            paginationInfo.setLimit(users.size());
//            paginationInfo.setTotalPages(1);
//            response.setPagination(paginationInfo);
//
//            return ResponseEntity.ok(response);
//        } catch (Exception e) {
//            log.error("Error getting inactive users: {}", e.getMessage(), e);
//
//            UserListResponseDTO response = new UserListResponseDTO();
//            response.setSuccess(false);
//            response.setMessage("An error occurred while retrieving inactive users");
//
//            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(response);
//        }
//    }
//
//    // Update profile picture
//    @PutMapping("/{userId}/profile-picture")
//    public ResponseEntity<ApiResponse> updateProfilePicture(
//            @PathVariable Integer userId,
//            @RequestParam String profilePictureUrl
//    ) {
//        log.info("PUT /api/v1/users/{}/profile-picture - Updating profile picture", userId);
//
//        try {
//            UserDto userDTO = userService.updateProfilePicture(userId, profilePictureUrl);
//
//            ApiResponse response = new ApiResponse();
//            response.setSuccess(true);
//            response.setMessage("Profile picture updated successfully");
//            response.setData(userDTO);
//
//            return ResponseEntity.ok(response);
//        } catch (RuntimeException e) {
//            log.error("Error updating profile picture: {}", e.getMessage());
//
//            ApiResponse response = new ApiResponse();
//            response.setSuccess(false);
//            response.setMessage(e.getMessage());
//
//            if (e.getMessage().contains("not found")) {
//                return ResponseEntity.status(HttpStatus.NOT_FOUND).body(response);
//            } else {
//                return ResponseEntity.badRequest().body(response);
//            }
//        } catch (Exception e) {
//            log.error("Unexpected error updating profile picture: {}", e.getMessage(), e);
//
//            ApiResponse response = new ApiResponse();
//            response.setSuccess(false);
//            response.setMessage("An unexpected error occurred");
//
//            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(response);
//        }
//    }
//
//    // Change password
//    @PutMapping("/{userId}/change-password")
//    public ResponseEntity<ApiResponse> changePassword(
//            @PathVariable Integer userId,
//            @RequestParam String newPassword
//    ) {
//        log.info("PUT /api/v1/users/{}/change-password - Changing password", userId);
//
//        try {
//            if (newPassword == null || newPassword.trim().length() < 6) {
//                throw new IllegalArgumentException("Password must be at least 6 characters");
//            }
//
//            UserDto userDTO = userService.changePassword(userId, newPassword.trim());
//
//            ApiResponse response = new ApiResponse();
//            response.setSuccess(true);
//            response.setMessage("Password changed successfully");
//            response.setData(userDTO);
//
//            return ResponseEntity.ok(response);
//        } catch (RuntimeException e) {
//            log.error("Error changing password: {}", e.getMessage());
//
//            ApiResponse response = new ApiResponse();
//            response.setSuccess(false);
//            response.setMessage(e.getMessage());
//
//            if (e.getMessage().contains("not found")) {
//                return ResponseEntity.status(HttpStatus.NOT_FOUND).body(response);
//            } else {
//                return ResponseEntity.badRequest().body(response);
//            }
//        } catch (Exception e) {
//            log.error("Unexpected error changing password: {}", e.getMessage(), e);
//
//            ApiResponse response = new ApiResponse();
//            response.setSuccess(false);
//            response.setMessage("An unexpected error occurred");
//
//            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(response);
//        }
//    }
//
//    // Get user statistics
//    @GetMapping("/statistics")
//    public ResponseEntity<ApiResponse> getUserStatistics() {
//        log.info("GET /api/v1/users/statistics - Getting user statistics");
//
//        try {
//            UserService.UserStatistics stats = userService.getStatistics();
//
//            // Create a response with statistics in the message
//            ApiResponse response = new ApiResponse();
//            response.setSuccess(true);
//            response.setMessage(String.format(
//                    "Total Users: %d, Admins: %d, Students: %d, Active: %d, Inactive: %d",
//                    stats.getTotalUsers(),
//                    stats.getAdminCount(),
//                    stats.getStudentCount(),
//                    stats.getActiveUsers(),
//                    stats.getInactiveUsers()
//            ));
//
//            // You could create a separate DTO for statistics if needed
//            // For now, we'll just put the stats in the message
//
//            return ResponseEntity.ok(response);
//        } catch (Exception e) {
//            log.error("Error getting user statistics: {}", e.getMessage(), e);
//
//            ApiResponse response = new ApiResponse();
//            response.setSuccess(false);
//            response.setMessage("An error occurred while retrieving statistics");
//
//            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(response);
//        }
//    }
}