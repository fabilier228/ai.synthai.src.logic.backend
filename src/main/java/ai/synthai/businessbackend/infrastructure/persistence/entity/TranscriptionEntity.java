package ai.synthai.businessbackend.infrastructure.persistence.entity;

import jakarta.persistence.Entity;
import jakarta.persistence.Table;
import jakarta.persistence.Index;
import jakarta.persistence.Id;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Column;
import jakarta.persistence.Lob;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDateTime;

@Entity
@Table(name = "transcription", indexes = {
        @Index(name = "idx_transcription_user", columnList = "keycloak_id")
})
@Getter
@Setter
@NoArgsConstructor
public class TranscriptionEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "keycloak_id", nullable = false)
    private String keycloakId;

    @Column(nullable = false)
    private String title;

    @Column(nullable = false)
    private String category;

    @Lob
    @Column
    private String transcript;

    @Lob
    @Column
    private String summary;

    @Column(name = "created_at", nullable = false)
    private LocalDateTime createdAt;

}
