package ai.synthai.businessbackend.application.dto;

import ai.synthai.businessbackend.domain.model.Category;
import ai.synthai.businessbackend.domain.model.Language;
import ai.synthai.businessbackend.domain.model.Status;
import lombok.Builder;

@Builder
public record TranscriptionResponseDto<T>(
        Status status,
        T transcriptionAnalysis,
        Category category,
        int duration,
        Language language) {
}
