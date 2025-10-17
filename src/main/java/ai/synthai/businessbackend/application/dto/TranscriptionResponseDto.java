package ai.synthai.businessbackend.application.dto;

import ai.synthai.businessbackend.domain.model.Category;
import ai.synthai.businessbackend.domain.model.Language;
import ai.synthai.businessbackend.domain.model.TranscriptionAnalysis;

public record TranscriptionResponseDto(
        String status,
        TranscriptionAnalysis transcriptionAnalysis,
        Category category,
        Float duration,
        Language language) {
}
