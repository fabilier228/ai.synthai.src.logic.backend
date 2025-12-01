package ai.synthai.businessbackend.application.dto;

import java.util.List;

public record DeepgramResponseDto(
        Metadata metadata,
        Results results
) {

    public record Metadata(
            String transaction_key,
            String request_id,
            String sha256,
            String created,
            int duration,
            int channels
    ) {}

    public record Results(
            List<Channel> channels
    ) {}

    public record Channel(
            List<Alternative> alternatives,
            String detected_language,
            double language_confidence
    ) {}

    public record Alternative(
    ) {}
}
