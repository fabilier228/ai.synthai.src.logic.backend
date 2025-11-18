package ai.synthai.businessbackend.application.dto;

import ai.synthai.businessbackend.domain.model.Status;
import ai.synthai.businessbackend.domain.model.Transcription;
import lombok.*;


@Getter
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class SingleTranscriptionDto {
    Status status;
    Transcription transcription;
}
