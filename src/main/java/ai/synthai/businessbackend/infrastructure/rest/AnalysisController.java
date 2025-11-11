package ai.synthai.businessbackend.infrastructure.rest;


import ai.synthai.businessbackend.application.dto.TranscriptionResponseDto;
import ai.synthai.businessbackend.application.service.SongTranscriptionService;
import ai.synthai.businessbackend.domain.model.Language;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

@RestController
@RequestMapping("/analysis")
@RequiredArgsConstructor
@Slf4j
public class AnalysisController {
    private final SongTranscriptionService songTranscriptionService;


    @PostMapping("/song")
    public ResponseEntity<TranscriptionResponseDto> analyzeSong(
            @RequestParam("audioFile") MultipartFile audioFile,
            @RequestParam(value = "language", required = false, defaultValue = "EN") Language language,
            @RequestParam("keycloakId") String keycloakId) {
        try {
            TranscriptionResponseDto response = songTranscriptionService.analyzeSong(audioFile, language, keycloakId);
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            log.error("Error during song analysis", e);
            return ResponseEntity.status(500).body(
                    TranscriptionResponseDto.builder()
                            .status(ai.synthai.businessbackend.domain.model.Status.FAILED)
                            .category(ai.synthai.businessbackend.domain.model.Category.SONG)
                            .language(language)
                            .build()
            );
        }
    }


    @PostMapping("/lecture")
    public ResponseEntity<String> analyzeLecture() {
        return ResponseEntity.ok("Text analysis endpoint is working!");
    }

    @PostMapping("/audiobook")
    public ResponseEntity<String> analyzeAudiobook() {
        return ResponseEntity.ok("Audiobook analysis endpoint is working!");
    }

    @PostMapping("/call")
    public ResponseEntity<String> analyzeCall() {
        return ResponseEntity.ok("Call analysis endpoint is working!");
    }
}
