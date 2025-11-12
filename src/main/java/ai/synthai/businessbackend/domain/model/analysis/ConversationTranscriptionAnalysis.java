package ai.synthai.businessbackend.domain.model.analysis;

import ai.synthai.businessbackend.domain.model.analysis.summary.ConversationSummary;
import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class ConversationTranscriptionAnalysis {
    String transcription;
    ConversationSummary summary;
}
