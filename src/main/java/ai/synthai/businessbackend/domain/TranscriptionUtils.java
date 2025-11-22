package ai.synthai.businessbackend.domain;

import ai.synthai.businessbackend.application.dto.TranscriptionResultDto;
import ai.synthai.businessbackend.domain.model.batch.transcription.response.Phrase;
import lombok.val;

import java.util.Comparator;

public class TranscriptionUtils {
    public static String createReadableDialogue(TranscriptionResultDto dto) {
        val stringBuilder = new StringBuilder();
        if (dto == null || dto.getPhrases() == null) {
            return stringBuilder.toString();
        }
        dto.getPhrases().stream()
                .sorted(Comparator.comparingInt(Phrase::getOffsetMilliseconds))
                .forEach(phrase -> {
                    int speaker = phrase.getSpeaker();
                    String text = phrase.getText();
                    stringBuilder.append("Speaker ")
                            .append(speaker)
                            .append(": ")
                            .append(text)
                            .append("\n");
                });

        return stringBuilder.toString();
    }

    public static String getText(TranscriptionResultDto dto) {
        return dto.getCombinedPhrases().get(0).text();
    }
}
