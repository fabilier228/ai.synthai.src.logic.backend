package ai.synthai.businessbackend.domain.model.analysis.summary;

import lombok.Builder;
import lombok.Getter;

import java.util.List;

@Builder
@Getter
public class LectureSummary {
    private String title;
    private String speaker;
    private String language;
    private String fieldOfStudy;
    private List<String> topics;
    private List<String> keyConcepts;
    private String tone;
    private List<String> structure;
    private String targetAudience;
    private String summary;
    private List<String> keyQuotes;
    private String mainArgument;
    private List<String> evidenceAndExamples;
    private String conclusion;
    private List<String> emotions;
    private String complexityLevel;
    private String purpose;
}
