package ai.synthai.businessbackend.domain.model.analysis;

import ai.synthai.businessbackend.domain.model.analysis.summary.AudiobookSummary;
import lombok.Builder;
import lombok.Getter;


@Getter
@Builder
public class AudiobookTranscriptionAnalysis {
    String transcription;
    AudiobookSummary summary;
}
