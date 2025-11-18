package ai.synthai.businessbackend.application.dto;

import ai.synthai.businessbackend.domain.model.Status;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Builder
@Getter
@NoArgsConstructor
@AllArgsConstructor
public class DeleteTranscriptionResponseDto {
    Status status;
    String message;
}
