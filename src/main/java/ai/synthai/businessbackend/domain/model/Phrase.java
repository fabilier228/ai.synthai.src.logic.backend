package ai.synthai.businessbackend.domain.model;

import java.util.List;

public record Phrase(
        int channel,
        int speaker,
        int offsetMilliseconds,
        int durationMilliseconds,
        String text,
        List<Word> words,
        String locale,
        double confidence
) {
}
