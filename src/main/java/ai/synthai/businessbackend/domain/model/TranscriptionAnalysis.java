package ai.synthai.businessbackend.domain.model;

import lombok.Builder;
import lombok.RequiredArgsConstructor;

import java.util.Map;

@Builder
public class TranscriptionAnalysis {
    String transcription;
    Map<String, Object> summary;
}
