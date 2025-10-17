package ai.synthai.businessbackend.domain.model;

import com.fasterxml.jackson.databind.JsonNode;

public record TranscriptionAnalysis(
        String transcription,
        JsonNode summary) {
}
