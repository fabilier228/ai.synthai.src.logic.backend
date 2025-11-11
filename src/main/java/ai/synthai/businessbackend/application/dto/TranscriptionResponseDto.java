package ai.synthai.businessbackend.application.dto;

import ai.synthai.businessbackend.domain.model.Category;
import ai.synthai.businessbackend.domain.model.Language;
import ai.synthai.businessbackend.domain.model.Status;
import ai.synthai.businessbackend.domain.model.TranscriptionAnalysis;
import lombok.Builder;

@Builder
public record TranscriptionResponseDto(
        Status status,
        TranscriptionAnalysis transcriptionAnalysis,
        Category category,
        Float duration,
        Language language) {
}
