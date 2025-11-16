package ai.synthai.businessbackend.application.dto;

import ai.synthai.businessbackend.domain.model.Status;
import lombok.Builder;

@Builder
public class DeleteTranscriptionResponseDto {
    Status status;
    String message;
}
