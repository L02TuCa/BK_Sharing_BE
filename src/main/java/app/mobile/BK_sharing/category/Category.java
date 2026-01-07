package app.mobile.BK_sharing.category;

import app.mobile.BK_sharing.document.entity.Document;
import app.mobile.BK_sharing.user.User;
import lombok.*;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import jakarta.persistence.*;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "category")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Category {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "category_id")
    private Long categoryId;

    @Column(name = "category_name", nullable = false, unique = true, length = 100)
    private String categoryName;

    @Column(name = "description", columnDefinition = "TEXT")
    private String description;

    @CreationTimestamp
    @Column(name = "created_at", nullable = false, updatable = false)
    private LocalDateTime createdAt;

     @ManyToOne(fetch = FetchType.LAZY)
     @JoinColumn(name = "created_by", referencedColumnName = "user_id", nullable = false)
     private User createdBy;

    @ManyToMany(mappedBy = "categories", fetch = FetchType.LAZY)
    private List<Document> documents = new ArrayList<>();
}