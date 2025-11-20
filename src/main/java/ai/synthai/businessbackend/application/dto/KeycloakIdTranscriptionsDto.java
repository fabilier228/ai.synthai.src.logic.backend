package ai.synthai.businessbackend.application.dto;

import ai.synthai.businessbackend.domain.model.Status;
import ai.synthai.businessbackend.domain.model.Transcription;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.util.List;

@Builder
@Getter
@NoArgsConstructor
@AllArgsConstructor
public class KeycloakIdTranscriptionsDto {
    Status status;
    List<Transcription> transcriptions;
}
