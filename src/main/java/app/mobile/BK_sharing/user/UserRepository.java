package app.mobile.BK_sharing.user;

import app.mobile.BK_sharing.user.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface UserRepository extends JpaRepository<User, Long> {

    // Find by email
    Optional<User> findByEmail(String email);

    // Find by username
    Optional<User> findByUsername(String username);

    // Check if email exists
    boolean existsByEmail(String email);

    // Check if email exists (excluding a specific user)
    boolean existsByEmailAndUserIdNot(String email, Long userId);

    // Check if username exists
    boolean existsByUsername(String username);

    // Check if username exists (excluding a specific user)
    boolean existsByUsernameAndUserIdNot(String username, Long userId);

    // Find users by role
    List<User> findByRole(User.UserRole role);

    // Find active users
    List<User> findByIsActiveTrue();

    // Find inactive users
    List<User> findByIsActiveFalse();

    // Find users by role and active status
    List<User> findByRoleAndIsActive(User.UserRole role, Boolean isActive);

    // Search users by name or email
    List<User> findByFullNameContainingIgnoreCaseOrEmailContainingIgnoreCase(String nameKeyword, String emailKeyword);

    // Search users by multiple criteria
    @Query("SELECT u FROM User u WHERE " +
            "(:keyword IS NULL OR " +
            "LOWER(u.username) LIKE LOWER(CONCAT('%', :keyword, '%')) OR " +
            "LOWER(u.email) LIKE LOWER(CONCAT('%', :keyword, '%')) OR " +
            "LOWER(u.fullName) LIKE LOWER(CONCAT('%', :keyword, '%'))) AND " +
            "(:role IS NULL OR u.role = :role) AND " +
            "(:isActive IS NULL OR u.isActive = :isActive)")
    List<User> searchUsers(
            @Param("keyword") String keyword,
            @Param("role") User.UserRole role,
            @Param("isActive") Boolean isActive
    );

    // Count users by role
    @Query("SELECT COUNT(u) FROM User u WHERE u.role = :role")
    Long countByRole(@Param("role") User.UserRole role);

    // Count active users
    @Query("SELECT COUNT(u) FROM User u WHERE u.isActive = true")
    Long countActiveUsers();
}