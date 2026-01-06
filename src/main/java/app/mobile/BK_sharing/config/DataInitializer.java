package app.mobile.BK_sharing.config;

import app.mobile.BK_sharing.user.User;
import app.mobile.BK_sharing.user.UserRepository;
import jakarta.annotation.PostConstruct;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Configuration;

import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.List;

@Configuration
@RequiredArgsConstructor
public class DataInitializer {

    private final UserRepository userRepository;

    @PostConstruct
    public void initializeData() {
        System.out.println("🚀 Checking and initializing default users...");

        // Only run if no users exist
        if (userRepository.count() == 0) {
            System.out.println("📝 Creating default users...");

            List<User> defaultUsers = Arrays.asList(
                    User.builder()
                            .username("admin_user")
                            .email("admin@bk.com")
                            .password("admin123")
                            .fullName("Admin User")
                            .role(User.UserRole.ADMIN)
                            .profilePicture("https://example.com/admin.jpg")
                            .isActive(true)
                            .createdAt(LocalDateTime.now())
                            .updatedAt(LocalDateTime.now())
                            .build(),

                    User.builder()
                            .username("student_one")
                            .email("student1@bk.com")
                            .password("student123")
                            .fullName("Student One")
                            .role(User.UserRole.STUDENT)
                            .profilePicture("https://example.com/student1.jpg")
                            .isActive(true)
                            .createdAt(LocalDateTime.now())
                            .updatedAt(LocalDateTime.now())
                            .build(),

                    User.builder()
                            .username("student_two")
                            .email("student2@bk.com")
                            .password("student123")
                            .fullName("Student Two")
                            .role(User.UserRole.STUDENT)
                            .profilePicture("https://example.com/student2.jpg")
                            .isActive(true)
                            .createdAt(LocalDateTime.now())
                            .updatedAt(LocalDateTime.now())
                            .build(),

                    User.builder()
                            .username("student_three")
                            .email("student3@bk.com")
                            .password("student123")
                            .fullName("Student Three")
                            .role(User.UserRole.STUDENT)
                            .profilePicture("https://example.com/student3.jpg")
                            .isActive(false)
                            .createdAt(LocalDateTime.now())
                            .updatedAt(LocalDateTime.now())
                            .build(),

                    User.builder()
                            .username("student_four")
                            .email("student4@bk.com")
                            .password("student123")
                            .fullName("Student Four")
                            .role(User.UserRole.STUDENT)
                            .profilePicture("https://example.com/student4.jpg")
                            .isActive(true)
                            .createdAt(LocalDateTime.now())
                            .updatedAt(LocalDateTime.now())
                            .build()
            );

            userRepository.saveAll(defaultUsers);
            System.out.println("✅ Successfully created " + defaultUsers.size() + " default users!");
        } else {
            System.out.println("✅ Users already exist. Skipping initialization.");
        }
    }
}