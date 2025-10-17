package ai.synthai.businessbackend.domain.model;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;

import java.time.LocalDateTime;

@Builder
@Getter
@AllArgsConstructor
public class Transcription {
    private Long id;
    private String keycloakId;
    private String title;
    private String category;
    private String transcript;
    private String summary;
    private LocalDateTime createdAt;
}
