package ai.synthai.businessbackend.domain.model.analysis.summary;

import lombok.Builder;
import lombok.Getter;

import java.util.List;


@Builder
@Getter
public class CallSummary {
    private List<String> participants;
    private String language;
    private String relationship;
    private String context;
    private List<String> topics;
    private String tone;
    private String summary;
    private List<String> emotions;
    private String conflictLevel;
    private String agreementOutcome;
    private List<String> keyQuotes;
}
