package app.mobile.BK_sharing.user;

import app.mobile.BK_sharing.storage.SupabaseStorageService;
import app.mobile.BK_sharing.user.dto.CreateUserDto;
import app.mobile.BK_sharing.user.dto.LoginDto;
import app.mobile.BK_sharing.user.dto.UpdateUserDTO;
import app.mobile.BK_sharing.user.dto.UserDto;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Slf4j
public class UserServiceImpl implements UserService {

    private final UserRepository userRepository;
    private final SupabaseStorageService storageService;

    @Override
    @Transactional
    public UserDto createUser(CreateUserDto createUserDTO) {
        log.info("Creating new user: {}", createUserDTO.getEmail());

        // Check if email already exists
        if (userRepository.existsByEmail(createUserDTO.getEmail())) {
            throw new IllegalArgumentException("Email already exists: " + createUserDTO.getEmail());
        }

        // Check if username already exists
        if (userRepository.existsByUsername(createUserDTO.getUsername())) {
            throw new IllegalArgumentException("Username already exists: " + createUserDTO.getUsername());
        }

        // Convert role string to enum - handle different cases
        User.UserRole role;
        String roleStr = createUserDTO.getRole();
        if (roleStr == null) {
            throw new IllegalArgumentException("Role is required");
        }

        // Handle case-insensitive role conversion
        try {
            role = User.UserRole.fromString(roleStr);
        } catch (IllegalArgumentException e) {
            throw new IllegalArgumentException("Invalid role. Must be 'Admin' or 'Student' (case-insensitive)");
        }

        // Create new user entity
        User user = User.builder()
                .username(createUserDTO.getUsername())
                .email(createUserDTO.getEmail())
                .password(createUserDTO.getPassword()) // In production, encrypt this!
                .fullName(createUserDTO.getFullName())
                .role(role)
                .profilePicture(createUserDTO.getProfilePicture() != null ? createUserDTO.getProfilePicture() :"https://mdygwitoqcbfhxohuazt.supabase.co/storage/v1/object/public/bk-docs/522a7d6b-23c2-4382-97ad-220cdeb11ae9.jpg")
                .isActive(createUserDTO.getIsActive() != null ? createUserDTO.getIsActive() : true)
        .build();
        
        
        User savedUser = userRepository.save(user);
        log.info("User created successfully with ID: {}", savedUser.getUserId());

        return UserDto.fromEntity(savedUser);
    }

    @Override
    @Transactional
    public UserDto loginUser(LoginDto loginDto){
        log.info("Login user by email: {}", loginDto.getEmail());
        User user = userRepository.findByEmail(loginDto.getEmail())
                .orElseThrow(() -> new RuntimeException("User not found with email: " + loginDto.getEmail()));

        if (!Objects.equals(loginDto.getPassword(), user.getPassword())) {
            log.warn("Invalid password attempt for email: {}", loginDto.getEmail());
            throw new RuntimeException("Invalid password");
        }

        log.info("User logged in successfully: {}", loginDto.getEmail());
        return UserDto.fromEntity(user);
    }


    @Override
    @Transactional(readOnly = true)
    public List<UserDto> getAllUsers() {
        log.info("Fetching all users");
        return userRepository.findAll().stream()
                .map(UserDto::fromEntity)
                .collect(Collectors.toList());
    }

    @Override
    @Transactional(readOnly = true)
    public Page<UserDto> getAllUsers(Pageable pageable) {
        log.info("Fetching all users with pagination: {}", pageable);
        return userRepository.findAll(pageable)
                .map(UserDto::fromEntity);
    }

    @Override
    @Transactional(readOnly = true)
    public UserDto getUserById(Long userId) {
        log.info("Fetching user by ID: {}", userId);
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new RuntimeException("User not found with ID: " + userId));

        return UserDto.fromEntity(user);
    }

    @Override
    @Transactional(readOnly = true)
    public UserDto getUserByEmail(String email) {
        log.info("Fetching user by email: {}", email);
        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new RuntimeException("User not found with email: " + email));

        return UserDto.fromEntity(user);
    }

    @Override
    @Transactional(readOnly = true)
    public UserDto getUserByUsername(String username) {
        log.info("Fetching user by username: {}", username);
        User user = userRepository.findByUsername(username)
                .orElseThrow(() -> new RuntimeException("User not found with username: " + username));

        return UserDto.fromEntity(user);
    }

    @Override
    @Transactional(readOnly = true)
    public List<UserDto> searchUsers(String keyword) {
        log.info("Searching users with keyword: {}", keyword);
        if (keyword == null || keyword.trim().isEmpty()) {
            return getAllUsers();
        }

        String searchKeyword = keyword.trim().toLowerCase();
        return userRepository.searchUsers(searchKeyword, null, null).stream()
                .map(UserDto::fromEntity)
                .collect(Collectors.toList());
    }

    @Override
    @Transactional(readOnly = true)
    public List<UserDto> getUsersByRole(String role) {
        log.info("Fetching users by role: {}", role);

        User.UserRole userRole;
        try {
            userRole = User.UserRole.fromString(role);
        } catch (IllegalArgumentException e) {
            throw new IllegalArgumentException("Invalid role. Must be 'Admin' or 'Student'");
        }

        return userRepository.findByRole(userRole).stream()
                .map(UserDto::fromEntity)
                .collect(Collectors.toList());
    }

    @Override
    @Transactional(readOnly = true)
    public List<UserDto> getActiveUsers() {
        log.info("Fetching active users");
        return userRepository.findByIsActiveTrue().stream()
                .map(UserDto::fromEntity)
                .collect(Collectors.toList());
    }

    @Override
    @Transactional(readOnly = true)
    public List<UserDto> getInactiveUsers() {
        log.info("Fetching inactive users");
        return userRepository.findByIsActiveFalse().stream()
                .map(UserDto::fromEntity)
                .collect(Collectors.toList());
    }

    @Override
    @Transactional
    public UserDto updateUser(Long userId, UpdateUserDTO updateUserDTO) {
        log.info("Updating user with ID: {}", userId);

        // Find existing user
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new RuntimeException("User not found with ID: " + userId));

        // Check if new email conflicts with existing users
        if (updateUserDTO.getEmail() != null &&
                !updateUserDTO.getEmail().equals(user.getEmail())) {

            if (userRepository.existsByEmailAndUserIdNot(updateUserDTO.getEmail(), userId)) {
                throw new IllegalArgumentException("Email already exists: " + updateUserDTO.getEmail());
            }
            user.setEmail(updateUserDTO.getEmail());
        }

        // Check if new username conflicts with existing users
        if (updateUserDTO.getUsername() != null &&
                !updateUserDTO.getUsername().equals(user.getUsername())) {

            if (userRepository.existsByUsernameAndUserIdNot(updateUserDTO.getUsername(), userId)) {
                throw new IllegalArgumentException("Username already exists: " + updateUserDTO.getUsername());
            }
            user.setUsername(updateUserDTO.getUsername());
        }

        // Update other fields if provided
        if (updateUserDTO.getPassword() != null) {
            user.setPassword(updateUserDTO.getPassword()); // In production, encrypt this!
        }

        if (updateUserDTO.getFullName() != null) {
            user.setFullName(updateUserDTO.getFullName());
        }

        if (updateUserDTO.getRole() != null) {
            try {
                User.UserRole role = User.UserRole.fromString(updateUserDTO.getRole());
                user.setRole(role);
            } catch (IllegalArgumentException e) {
                throw new IllegalArgumentException("Invalid role. Must be 'Admin' or 'Student'");
            }
        }

        if (updateUserDTO.getProfilePicture() != null) {
            user.setProfilePicture(updateUserDTO.getProfilePicture());
        }

        if (updateUserDTO.getIsActive() != null) {
            user.setIsActive(updateUserDTO.getIsActive());
        }

        // Save updated user
        User updatedUser = userRepository.save(user);
        log.info("User updated successfully with ID: {}", userId);

        return UserDto.fromEntity(updatedUser);
    }

    @Override
    @Transactional
    public void deactivateUser(Long userId) {
        log.info("Deactivating user with ID: {}", userId);

        User user = userRepository.findById(userId)
                .orElseThrow(() -> new RuntimeException("User not found with ID: " + userId));

        user.setIsActive(false);
        userRepository.save(user);

        log.info("User deactivated successfully with ID: {}", userId);
    }

    @Override
    @Transactional
    public void deleteUser(Long userId) {
        log.info("Deleting user with ID: {}", userId);

        // Check if user exists
        if (!userRepository.existsById(userId)) {
            throw new RuntimeException("User not found with ID: " + userId);
        }

        // Optional: Check if user has associated data
        // This would require DocumentRepository, CategoryRepository dependencies
        // You might want to implement cascading delete or prevent deletion

        // Delete user
        userRepository.deleteById(userId);
        log.info("User deleted successfully with ID: {}", userId);
    }

    @Override
    @Transactional
    public UserDto activateUser(Long userId) {
        log.info("Activating user with ID: {}", userId);

        User user = userRepository.findById(userId)
                .orElseThrow(() -> new RuntimeException("User not found with ID: " + userId));

        user.setIsActive(true);
        User activatedUser = userRepository.save(user);

        log.info("User activated successfully with ID: {}", userId);
        return UserDto.fromEntity(activatedUser);
    }






//    Image

    @Transactional
    public User updateProfilePicture(Long userId, String newImageUrl) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new RuntimeException("User not found with id: " + userId));

        // Delete old profile picture if exists
        if (user.getProfilePicture() != null && !user.getProfilePicture().isEmpty()) {
            try {
                storageService.deleteFile(user.getProfilePicture());
            } catch (Exception e) {
                log.warn("Failed to delete old profile picture: {}", e.getMessage());
                // Continue anyway - don't fail the update if deletion fails
            }
        }

        user.setProfilePicture(newImageUrl);
        return userRepository.save(user);
    }

    @Transactional
    public void deleteProfilePicture(Long userId) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new RuntimeException("User not found with id: " + userId));

        if (user.getProfilePicture() != null && !user.getProfilePicture().isEmpty()) {
            try {
                storageService.deleteFile(user.getProfilePicture());
                user.setProfilePicture(null);
                userRepository.save(user);
            } catch (Exception e) {
                throw new RuntimeException("Failed to delete profile picture: " + e.getMessage(), e);
            }
        }
    }

    @Override
    @Transactional(readOnly = true)
    public boolean existsById(Long userId) {
        return userRepository.existsById(userId);
    }

    @Override
    @Transactional(readOnly = true)
    public boolean existsByEmail(String email) {
        return userRepository.existsByEmail(email);
    }

    @Override
    @Transactional(readOnly = true)
    public boolean existsByUsername(String username) {
        return userRepository.existsByUsername(username);
    }

//    @Override
//    @Transactional(readOnly = true)
//    public UserStatistics getStatistics() {
//        log.info("Getting user statistics");
//
//        UserStatistics stats = new UserStatistics();
//
//        Long totalUsers = userRepository.count();
//        Long adminCount = userRepository.countByRole(User.UserRole.ADMIN);
//        Long studentCount = userRepository.countByRole(User.UserRole.STUDENT);
//        Long activeUsers = userRepository.countActiveUsers();
//
//        stats.setTotalUsers(totalUsers);
//        stats.setAdminCount(adminCount);
//        stats.setStudentCount(studentCount);
//        stats.setActiveUsers(activeUsers);
//        stats.setInactiveUsers(totalUsers - activeUsers);
//
//        return stats;
//    }

//    @Override
//    @Transactional
//    public UserDto updateProfilePicture(Long userId, String profilePictureUrl) {
//        log.info("Updating profile picture for user ID: {}", userId);
//
//        User user = userRepository.findById(userId)
//                .orElseThrow(() -> new RuntimeException("User not found with ID: " + userId));
//
//        user.setProfilePicture(profilePictureUrl);
//        User updatedUser = userRepository.save(user);
//
//        log.info("Profile picture updated successfully for user ID: {}", userId);
//        return UserDto.fromEntity(updatedUser);
//    }

    @Override
    @Transactional
    public UserDto changePassword(Long userId, String newPassword) {
        log.info("Changing password for user ID: {}", userId);

        User user = userRepository.findById(userId)
                .orElseThrow(() -> new RuntimeException("User not found with ID: " + userId));

        user.setPassword(newPassword); // In production, encrypt this!
        User updatedUser = userRepository.save(user);

        log.info("Password changed successfully for user ID: {}", userId);
        return UserDto.fromEntity(updatedUser);
    }
}