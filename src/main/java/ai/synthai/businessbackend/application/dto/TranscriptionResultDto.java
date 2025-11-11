package ai.synthai.businessbackend.application.dto;

import ai.synthai.businessbackend.domain.model.CombinedPhrase;
import ai.synthai.businessbackend.domain.model.Phrase;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;

import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class TranscriptionResultDto {

    private long durationMilliseconds;

    private List<CombinedPhrase> combinedPhrases;

    private List<Phrase> phrases;
}
