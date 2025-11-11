package ai.synthai.businessbackend.domain.model.analysis;

import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class EmotionalTranscriptionAnalysis {
    private String transcription;
    private String emotion;
}
