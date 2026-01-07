package app.mobile.BK_sharing.document.entity;

import app.mobile.BK_sharing.category.Category;
import app.mobile.BK_sharing.course.Course;
import app.mobile.BK_sharing.user.User;
import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "document")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Document {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "document_id")
    private Long documentId;

    @Column(name = "title", nullable = false, length = 255)
    private String title;

    @Column(name = "description", columnDefinition = "TEXT")
    private String description;

    @Enumerated(EnumType.STRING)
    @Column(name = "file_type", nullable = false, length = 20)
    private FileType fileType;

    @Column(name = "file_path", nullable = false, length = 500)
    private String filePath;

    @Column(name = "file_size", nullable = false)
    private Long fileSize;

    @ManyToMany(fetch = FetchType.LAZY)
    @JoinTable(
            name = "document_categories",  // Join table name
            joinColumns = @JoinColumn(name = "document_id"),  // This entity's key
            inverseJoinColumns = @JoinColumn(name = "category_id")  // Other entity's key
    )
    private List<Category> categories = new ArrayList<>();

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "course_id")  // Foreign key in Employee table
    private Course course;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "uploaded_by", referencedColumnName = "user_id", nullable = false)
    private User uploadedBy;

    @Column(name = "is_approved", nullable = false)
    private Boolean isApproved = false;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "approved_by", referencedColumnName = "user_id")
    private User approvedBy;

    @CreationTimestamp
    @Column(name = "created_at", nullable = false, updatable = false)
    private LocalDateTime createdAt;

    @UpdateTimestamp
    @Column(name = "updated_at", nullable = false)
    private LocalDateTime updatedAt;

    // Enum for file type
    public enum FileType {
        PDF("PDF"),
        WORD("WORD"),
        POWERPOINT("POWERPOINT"),
        OTHER("OTHER");

        private final String value;

        FileType(String value) {
            this.value = value;
        }

        public String getValue() {
            return value;
        }

        public static FileType fromString(String value) {
            for (FileType type : FileType.values()) {
                if (type.value.equalsIgnoreCase(value)) {
                    return type;
                }
            }
            throw new IllegalArgumentException("Invalid file type: " + value);
        }
    }
}