package app.mobile.BK_sharing.user;

import app.mobile.BK_sharing.category.Category;
import app.mobile.BK_sharing.document.entity.Document;
import app.mobile.BK_sharing.document.entity.DocumentAccess;
import app.mobile.BK_sharing.document.entity.DocumentInteraction;
import app.mobile.BK_sharing.document.entity.DocumentVersion;
import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import java.time.LocalDateTime;
import java.util.List;

@Entity
@Table(name = "app_user")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class User {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "user_id")
    private Long userId;

    @Column(name = "username", nullable = false, unique = true, length = 100)
    private String username;

    @Column(name = "email", nullable = false, unique = true, length = 100)
    private String email;

    @Column(name = "password", nullable = false, length = 255)
    private String password;

    @Column(name = "full_name", nullable = false, length = 255)
    private String fullName;

    @Enumerated(EnumType.STRING)
    @Column(name = "role", nullable = false, length = 10)
    private UserRole role;

    @OneToMany(mappedBy = "createdBy", fetch = FetchType.LAZY)
    private List<Category> createdCategories;

    // Documents uploaded by this user
    @OneToMany(mappedBy = "uploadedBy", fetch = FetchType.LAZY)
    private List<Document> uploadedDocuments;

    // Documents approved by this user (as admin)
    @OneToMany(mappedBy = "approvedBy", fetch = FetchType.LAZY)
    private List<Document> approvedDocuments;

    // Document versions edited by this user
    @OneToMany(mappedBy = "editedBy", fetch = FetchType.LAZY)
    private List<DocumentVersion> editedVersions;

    // Document access granted by this user
    @OneToMany(mappedBy = "grantedBy", fetch = FetchType.LAZY)
    private List<DocumentAccess> grantedAccesses;

    // Document access granted to this user
    @OneToMany(mappedBy = "user", fetch = FetchType.LAZY)
    private List<DocumentAccess> documentAccesses;

    // Document interactions by this user
    @OneToMany(mappedBy = "user", fetch = FetchType.LAZY)
    private List<DocumentInteraction> documentInteractions;

    @Column(name = "profile_picture", length = 500)
    private String profilePicture;

    @Column(name = "is_active", nullable = false)
    private Boolean isActive = true;

    @CreationTimestamp
    @Column(name = "created_at", nullable = false, updatable = false)
    private LocalDateTime createdAt;

    @UpdateTimestamp
    @Column(name = "updated_at", nullable = false)
    private LocalDateTime updatedAt;

    // Enum for user role
    @Getter
    public enum UserRole {
        ADMIN,   // Just ADMIN (not ADMIN("Admin"))
        STUDENT;   // Just STUDENT (not STUDENT("Student"))

        // Remove the displayName field and constructor

        public static UserRole fromString(String value) {
            if (value == null) {
                throw new IllegalArgumentException("Role cannot be null");
            }

            try {
                // Try exact match (handles "ADMIN", "STUDENT")
                return UserRole.valueOf(value.toUpperCase());
            } catch (IllegalArgumentException e) {
                // Handle case-insensitive
                if ("admin".equalsIgnoreCase(value) || "administrator".equalsIgnoreCase(value)) {
                    return ADMIN;
                } else if ("student".equalsIgnoreCase(value) || "learner".equalsIgnoreCase(value)) {
                    return STUDENT;
                }
                throw new IllegalArgumentException("Invalid role: " + value + ". Must be 'Admin' or 'Student'");
            }
        }
    }
}