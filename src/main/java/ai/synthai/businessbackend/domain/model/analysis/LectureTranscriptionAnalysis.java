package ai.synthai.businessbackend.domain.model.analysis;

import ai.synthai.businessbackend.domain.model.analysis.summary.LectureSummary;
import lombok.Builder;
import lombok.Getter;


@Builder
@Getter
public class LectureTranscriptionAnalysis {
    String transcription;
    LectureSummary summary;
}
