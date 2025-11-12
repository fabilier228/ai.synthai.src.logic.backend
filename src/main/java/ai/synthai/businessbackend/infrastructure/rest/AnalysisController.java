package ai.synthai.businessbackend.infrastructure.rest;


import ai.synthai.businessbackend.application.dto.TranscriptionResponseDto;
import ai.synthai.businessbackend.application.service.AudiobookTranscriptionService;
import ai.synthai.businessbackend.application.service.ConversationTranscriptionService;
import ai.synthai.businessbackend.application.service.LectureTranscriptionService;
import ai.synthai.businessbackend.application.service.SongTranscriptionService;
import ai.synthai.businessbackend.domain.model.Category;
import ai.synthai.businessbackend.domain.model.Language;
import ai.synthai.businessbackend.domain.model.Status;
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
    private final LectureTranscriptionService lectureTranscriptionService;
    private final ConversationTranscriptionService conversationTranscriptionService;
    private final AudiobookTranscriptionService audiobookTranscriptionService;
    @PostMapping("/song")
    public ResponseEntity<TranscriptionResponseDto> analyzeSong(
            @RequestParam("audioFile") MultipartFile audioFile,
            @RequestParam(value = "language", required = false, defaultValue = "EN") Language language,
            @RequestParam("keycloakId") String keycloakId,
            @RequestParam("title") String title) {
        try {
            log.info("Received song analysis request: keycloakId={}, title={}, language={}", keycloakId, title, language);
            TranscriptionResponseDto response = songTranscriptionService.analyzeSong(audioFile, language, keycloakId, title);
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            log.error("Error during song analysis", e);
            return ResponseEntity.status(500).body(
                    TranscriptionResponseDto.builder()
                            .status(Status.FAILED)
                            .category(Category.SONG)
                            .language(language)
                            .build()
            );
        }
    }


    @PostMapping("/lecture")
    public ResponseEntity<TranscriptionResponseDto> analyzeLecture(
            @RequestParam("audioFile") MultipartFile audioFile,
            @RequestParam(value = "language", required = false, defaultValue = "EN") Language language,
            @RequestParam("keycloakId") String keycloakId,
            @RequestParam("title") String title) {
        try {
            TranscriptionResponseDto response = lectureTranscriptionService.analyzeLecture(audioFile, language, keycloakId, title);
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            log.error("Error during lecture analysis", e);
            return ResponseEntity.status(500).body(
                    TranscriptionResponseDto.builder()
                            .status(Status.FAILED)
                            .category(Category.LECTURE)
                            .language(language)
                            .build());
        }
    }

    @PostMapping("/audiobook")
    public ResponseEntity<TranscriptionResponseDto> analyzeAudiobook(
            @RequestParam("audioFile") MultipartFile audioFile,
            @RequestParam(value = "language", required = false, defaultValue = "EN") Language language,
            @RequestParam("keycloakId") String keycloakId,
            @RequestParam("title") String title) {
        try {
            TranscriptionResponseDto response = audiobookTranscriptionService.analyzeAudiobook(audioFile, language, keycloakId, title);
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            log.error("Error during audiobook analysis", e);
            return ResponseEntity.status(500).body(
                    TranscriptionResponseDto
                            .builder()
                            .status(Status.FAILED)
                            .category(Category.AUDIOBOOK)
                            .language(language)
                            .build());
        }
    }

    @PostMapping("/conversation")
    public ResponseEntity<TranscriptionResponseDto> analyzeConversation(
            @RequestParam("audioFile") MultipartFile audioFile,
            @RequestParam(value = "language", required = false, defaultValue = "EN") Language language,
            @RequestParam("keycloakId") String keycloakId,
            @RequestParam("title") String title
    ) {
        try {
            TranscriptionResponseDto response = conversationTranscriptionService.analyzeConversation(audioFile, language, keycloakId, title);
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            log.error("Error during call analysis", e);
            return ResponseEntity.status(500).body(
                    TranscriptionResponseDto.builder()
                            .status(Status.FAILED)
                            .category(Category.CONVERSATION)
                            .language(language)
                            .build());
        }
    }

    @PostMapping("/test")
    public ResponseEntity<String> testEndpoint() {
        log.info("Test endpoint reached");
        return ResponseEntity.ok("Test endpoint is working!");
    }
}
