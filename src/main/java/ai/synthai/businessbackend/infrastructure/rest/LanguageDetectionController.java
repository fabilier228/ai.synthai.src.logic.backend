package ai.synthai.businessbackend.infrastructure.rest;

import ai.synthai.businessbackend.application.service.LanguageDetectionService;
import ai.synthai.businessbackend.domain.model.Language;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import lombok.val;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

@RestController
@RequestMapping
@RequiredArgsConstructor
@Slf4j
public class LanguageDetectionController {
    private final LanguageDetectionService languageDetectionService;


    @PostMapping("/detect-language")
    public ResponseEntity<Language> detectLanguage(@RequestParam MultipartFile audioFile) {
        try {
            val language = languageDetectionService.detectLanguage(audioFile);
            return ResponseEntity.status(200).body(language);
        } catch (Exception e) {
            log.error("Error during language detection: {}", e.getMessage());
            return ResponseEntity.status(500).body(null);
        }
    }

}
