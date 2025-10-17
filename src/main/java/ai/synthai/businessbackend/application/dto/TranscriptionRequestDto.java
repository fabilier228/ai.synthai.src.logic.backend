package ai.synthai.businessbackend.application.dto;

import ai.synthai.businessbackend.domain.model.Category;
import ai.synthai.businessbackend.domain.model.Language;
import org.springframework.web.multipart.MultipartFile;

public record TranscriptionRequestDto(Category category,
                                      MultipartFile audioFile,
                                      Language language) {


}
