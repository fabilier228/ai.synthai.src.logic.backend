package ai.synthai.businessbackend.infrastructure.persistence;

import ai.synthai.businessbackend.domain.model.Transcription;
import ai.synthai.businessbackend.infrastructure.persistence.entity.TranscriptionEntity;
import lombok.NoArgsConstructor;
import org.springframework.stereotype.Component;

@Component
@NoArgsConstructor
public final class TranscriptionMapper {

    public static TranscriptionEntity toEntity(final Transcription transcription) {
        TranscriptionEntity entity = new TranscriptionEntity();
        entity.setId(transcription.getId());
        entity.setKeycloakId(transcription.getKeycloakId());
        entity.setTitle(transcription.getTitle());
        entity.setCategory(transcription.getCategory());
        entity.setTranscript(transcription.getTranscript());
        entity.setSummary(transcription.getSummary());
        entity.setCreatedAt(transcription.getCreatedAt());
        return entity;
    }

    public static Transcription toDomain(final TranscriptionEntity transcriptionEntity) {
        return Transcription.builder()
            .id(transcriptionEntity.getId())
            .keycloakId(transcriptionEntity.getKeycloakId())
            .title(transcriptionEntity.getTitle())
            .category(transcriptionEntity.getCategory())
            .transcript(transcriptionEntity.getTranscript())
            .summary(transcriptionEntity.getSummary())
            .createdAt(
                transcriptionEntity.getCreatedAt()
            )
            .build();
    }
}
