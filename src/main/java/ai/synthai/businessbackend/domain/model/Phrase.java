package ai.synthai.businessbackend.domain.model;

import lombok.Builder;
import lombok.Getter;

import java.util.List;

@Builder
@Getter
public class Phrase {
    int channel;
    int speaker;
    int offsetMilliseconds;
    int durationMilliseconds;
    String text;
    List<Word> words;
    String locale;
    double confidence;
}
