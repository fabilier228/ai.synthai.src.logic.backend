package ai.synthai.businessbackend.infrastructure.persistence;

import ai.synthai.businessbackend.domain.model.Transcription;
import ai.synthai.businessbackend.infrastructure.persistence.entity.TranscriptionEntity;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.extern.slf4j.Slf4j;
import com.fasterxml.jackson.core.JsonProcessingException;
import org.springframework.stereotype.Component;

@Slf4j
@Component
public final class TranscriptionMapper {

    private static final ObjectMapper objectMapper = new ObjectMapper();

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

    public static String summaryToJsonString(final Object summaryObject) {
        if (summaryObject == null) return null;
        try {
            return objectMapper.writeValueAsString(summaryObject);
        } catch (JsonProcessingException e) {
            log.error("Error serializing summary to JSON", e);
            return "{}";
        }
    }
}
