package ai.synthai.businessbackend.domain.model;

import lombok.Builder;
import lombok.Getter;

import java.util.Map;

@Builder
@Getter
public class TranscriptionAnalysis {
    String transcription;
    Map<String, Object> summary;
}
