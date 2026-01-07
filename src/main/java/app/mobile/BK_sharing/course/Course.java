package app.mobile.BK_sharing.course;

import app.mobile.BK_sharing.document.entity.Document;
import app.mobile.BK_sharing.user.User;
import lombok.*;
import org.hibernate.annotations.CreationTimestamp;

import jakarta.persistence.*;
import java.time.LocalDateTime;
import java.util.List;

@Entity
@Table(name = "course")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Course {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "course_id")
    private Long courseId;

    @Column(name = "course_code", nullable = false, unique = true, length = 50)
    private String courseCode; // e.g., "CS101", "MATH202"

    @Column(name = "course_name", nullable = false, length = 200)
    private String courseName; // e.g., "Introduction to Programming"

    @Column(name = "description", columnDefinition = "TEXT")
    private String description;

    @CreationTimestamp
    @Column(name = "created_at", nullable = false, updatable = false)
    private LocalDateTime createdAt;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "created_by", referencedColumnName = "user_id", nullable = false)
    private User createdBy;

    // Many-to-many relationship with documents
    @OneToMany(mappedBy = "category", fetch = FetchType.LAZY)
    private List<Document> documents;
}