package ai.synthai.businessbackend.application.dto;

import ai.synthai.businessbackend.domain.model.Status;
import ai.synthai.businessbackend.domain.model.Transcription;
import lombok.Builder;

import java.util.List;

@Builder
public class KeycloakIdTranscriptionsDto {
    Status status;
    List<Transcription> transcriptions;
}
