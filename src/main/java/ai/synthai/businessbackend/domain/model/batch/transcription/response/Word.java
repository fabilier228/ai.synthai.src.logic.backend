package ai.synthai.businessbackend.domain.model.batch.transcription.response;

public record Word(
    String text,
    long offsetMilliseconds,
    long durationMilliseconds
) {
}
