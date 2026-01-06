package app.mobile.BK_sharing.document.entity;

import app.mobile.BK_sharing.user.User;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.CreationTimestamp;

import java.time.LocalDateTime;

@Entity
@Table(name = "document_interaction")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class DocumentInteraction {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "interaction_id")
    private Long interactionId;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "document_id", referencedColumnName = "document_id", nullable = false)
    private Document document;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", referencedColumnName = "user_id", nullable = false)
    private User user;

    @Enumerated(EnumType.STRING)
    @Column(name = "interaction_type", nullable = false)
    private InteractionType interactionType;

    @CreationTimestamp
    @Column(name = "interaction_at", nullable = false)
    private LocalDateTime interactionAt;

    // Enum for interaction type
    public enum InteractionType {
        View("View"),
        Download("Download");

        private final String value;

        InteractionType(String value) {
            this.value = value;
        }

        public String getValue() {
            return value;
        }

        public static InteractionType fromString(String value) {
            for (InteractionType type : InteractionType.values()) {
                if (type.value.equalsIgnoreCase(value)) {
                    return type;
                }
            }
            throw new IllegalArgumentException("Invalid interaction type: " + value);
        }
    }
}