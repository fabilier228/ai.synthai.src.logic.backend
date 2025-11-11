package ai.synthai.businessbackend.domain.model.analysis;

import ai.synthai.businessbackend.domain.model.analysis.summary.CallSummary;
import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class CallTranscriptionAnalysis {
    String transcription;
    CallSummary summary;
}
