package app.mobile.BK_sharing.document.entity;

import app.mobile.BK_sharing.user.User;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.CreationTimestamp;

import java.time.LocalDateTime;

@Entity
@Table(name = "document_access")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class DocumentAccess {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "access_id")
    private Long accessId;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "document_id", referencedColumnName = "document_id", nullable = false)
    private Document document;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", referencedColumnName = "user_id", nullable = false)
    private User user;

    @Column(name = "can_view", nullable = false)
    private Boolean canView = false;

    @Column(name = "can_download", nullable = false)
    private Boolean canDownload = false;

    @Column(name = "can_edit", nullable = false)
    private Boolean canEdit = false;

    @CreationTimestamp
    @Column(name = "granted_at", nullable = false)
    private LocalDateTime grantedAt;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "granted_by", referencedColumnName = "user_id", nullable = false)
    private User grantedBy;
}