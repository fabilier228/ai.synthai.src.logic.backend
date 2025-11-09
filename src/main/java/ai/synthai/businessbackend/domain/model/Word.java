package ai.synthai.businessbackend.domain.model;

public record Word(
    String text,
    long offsetMilliseconds,
    long durationMilliseconds
) {
}
