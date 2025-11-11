package ai.synthai.businessbackend.application.dto;

import ai.synthai.businessbackend.domain.model.batch.transcription.response.CombinedPhrase;
import ai.synthai.businessbackend.domain.model.batch.transcription.response.Phrase;
import lombok.*;

import java.util.List;

@Data
@Builder
@Getter
@AllArgsConstructor
public class TranscriptionResultDto {

    private long durationMilliseconds;

    private List<CombinedPhrase> combinedPhrases;

    private List<Phrase> phrases;
}
