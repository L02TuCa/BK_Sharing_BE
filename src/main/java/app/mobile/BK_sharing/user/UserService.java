package app.mobile.BK_sharing.user;

import app.mobile.BK_sharing.user.dto.CreateUserDto;
import app.mobile.BK_sharing.user.dto.UpdateUserDTO;
import app.mobile.BK_sharing.user.dto.UserDto;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.List;

public interface UserService {

    // Create a new user
    UserDto createUser(CreateUserDto createUserDTO);

    // Get all users
    List<UserDto> getAllUsers();

    // Get all users with pagination
    Page<UserDto> getAllUsers(Pageable pageable);

    // Get user by ID
    UserDto getUserById(Long userId);

    // Get user by email
    UserDto getUserByEmail(String email);

    // Get user by username
    UserDto getUserByUsername(String username);

    // Search users
    List<UserDto> searchUsers(String keyword);

    // Get users by role
    List<UserDto> getUsersByRole(String role);

    // Get active users
    List<UserDto> getActiveUsers();

    // Get inactive users
    List<UserDto> getInactiveUsers();

    // Update user
    UserDto updateUser(Long userId, UpdateUserDTO updateUserDTO);

    // Delete user (soft delete by setting isActive to false)
    void deactivateUser(Long userId);

    // Delete user permanently
    void deleteUser(Long userId);

    // Activate user
    UserDto activateUser(Long userId);

    // Check if user exists
    boolean existsById(Long userId);

    // Check if email exists
    boolean existsByEmail(String email);

    // Check if username exists
    boolean existsByUsername(String username);

    // Get user statistics
    UserStatistics getStatistics();

    // Update user profile picture
    UserDto updateProfilePicture(Long userId, String profilePictureUrl);

    // Change user password
    UserDto changePassword(Long userId, String newPassword);

    // User statistics DTO
    class UserStatistics {
        private Long totalUsers;
        private Long adminCount;
        private Long studentCount;
        private Long activeUsers;
        private Long inactiveUsers;

        // Getters and setters
        public Long getTotalUsers() { return totalUsers; }
        public void setTotalUsers(Long totalUsers) { this.totalUsers = totalUsers; }

        public Long getAdminCount() { return adminCount; }
        public void setAdminCount(Long adminCount) { this.adminCount = adminCount; }

        public Long getStudentCount() { return studentCount; }
        public void setStudentCount(Long studentCount) { this.studentCount = studentCount; }

        public Long getActiveUsers() { return activeUsers; }
        public void setActiveUsers(Long activeUsers) { this.activeUsers = activeUsers; }

        public Long getInactiveUsers() { return inactiveUsers; }
        public void setInactiveUsers(Long inactiveUsers) { this.inactiveUsers = inactiveUsers; }
    }
}