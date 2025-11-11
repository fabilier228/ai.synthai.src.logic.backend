package ai.synthai.businessbackend.application.dto;

import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class EmotionalTranscriptionResponseDto {
    private String transcription;
    private String emotion;
}
