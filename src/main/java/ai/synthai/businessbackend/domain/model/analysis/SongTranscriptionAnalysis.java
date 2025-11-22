package ai.synthai.businessbackend.domain.model.analysis;

import ai.synthai.businessbackend.domain.model.analysis.summary.SongSummary;
import lombok.Builder;
import lombok.Getter;


@Getter
@Builder
public class SongTranscriptionAnalysis {
    String transcription;
    SongSummary summary;
}
