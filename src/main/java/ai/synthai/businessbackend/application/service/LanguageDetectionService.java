package ai.synthai.businessbackend.application.service;


import ai.synthai.businessbackend.domain.model.Language;
import ai.synthai.businessbackend.infrastructure.client.AudioLanguageDetection;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

@Service
@RequiredArgsConstructor
public class LanguageDetectionService {
    private final AudioLanguageDetection audioLanguageDetection;

    public Language detectLanguage(MultipartFile audioFile) throws Exception {
        byte[] audioFileBytes;
        try {
            audioFileBytes = audioFile.getBytes();
        } catch (Exception e) {
            throw new Exception("Failed to read audio file", e);
        }

        String detectedLanguageCode = audioLanguageDetection.detectLanguage(audioFileBytes);
        if (detectedLanguageCode == null) {
            return null;
        }
        return switch (detectedLanguageCode) {
            case "en" -> Language.ENGLISH;
            case "pl" -> Language.POLISH;
            default -> null;
        };
    }
}
