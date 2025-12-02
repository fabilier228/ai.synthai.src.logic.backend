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
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

@RestController
@RequestMapping("/analysis")
@RequiredArgsConstructor
@Slf4j
public class AnalysisController {
    private final SongTranscriptionService songTranscriptionService;
    private final LectureTranscriptionService lectureTranscriptionService;
    private final ConversationTranscriptionService conversationTranscriptionService;
    private final AudiobookTranscriptionService audiobookTranscriptionService;

    // Pomocnicza metoda do bezpiecznego pobierania listy
    private List<String> safeList(List<String> list) {
        return list != null ? list : new ArrayList<>();
    }

    private ResponseEntity<TranscriptionResponseDto> handleError(Exception e, Category category, Language language) {
        // TO JEST KLUCZOWE - wypisze błąd w konsoli serwera (IntelliJ)
        log.error("CRITICAL ERROR in " + category + " analysis:", e);
        e.printStackTrace(); 
        
        return ResponseEntity.status(500).body(
                TranscriptionResponseDto.builder()
                        .status(Status.FAILED)
                        .category(category)
                        .language(language)
                        .build()
        );
    }

    @PostMapping("/song")
    public ResponseEntity<TranscriptionResponseDto> analyzeSong(
            @RequestParam("audioFile") MultipartFile audioFile,
            @RequestParam("language") Language language,
            @RequestParam("keycloakId") String keycloakId,
            @RequestParam("title") String title,
            @RequestParam(value = "temperature", defaultValue = "0.0") Double temperature,
            @RequestParam(value = "diarization", defaultValue = "false") Boolean diarization,
            @RequestParam(value = "phraseList", required = false) List<String> phraseList
    ) {
        try {
            log.info("Received request for SONG: title={}, temp={}, diarization={}, phrases={}", 
                    title, temperature, diarization, phraseList);

            TranscriptionResponseDto response = songTranscriptionService.analyzeSong(
                    audioFile, language, keycloakId, title, temperature, diarization, safeList(phraseList));
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            return handleError(e, Category.SONG, language);
        }
    }

    @PostMapping("/lecture")
    public ResponseEntity<TranscriptionResponseDto> analyzeLecture(
            @RequestParam("audioFile") MultipartFile audioFile,
            @RequestParam("language") Language language,
            @RequestParam("keycloakId") String keycloakId,
            @RequestParam("title") String title,
            @RequestParam(value = "temperature", defaultValue = "0.0") Double temperature,
            @RequestParam(value = "diarization", defaultValue = "false") Boolean diarization,
            @RequestParam(value = "phraseList", required = false) List<String> phraseList
    ) {
        try {
            TranscriptionResponseDto response = lectureTranscriptionService.analyzeLecture(
                    audioFile, language, keycloakId, title, temperature, diarization, safeList(phraseList));
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            return handleError(e, Category.LECTURE, language);
        }
    }

    @PostMapping("/audiobook")
    public ResponseEntity<TranscriptionResponseDto> analyzeAudiobook(
            @RequestParam("audioFile") MultipartFile audioFile,
            @RequestParam("language") Language language,
            @RequestParam("keycloakId") String keycloakId,
            @RequestParam("title") String title,
            @RequestParam(value = "temperature", defaultValue = "0.0") Double temperature,
            @RequestParam(value = "diarization", defaultValue = "false") Boolean diarization,
            @RequestParam(value = "phraseList", required = false) List<String> phraseList
    ) {
        try {
            TranscriptionResponseDto response = audiobookTranscriptionService.analyzeAudiobook(
                    audioFile, language, keycloakId, title, temperature, diarization, safeList(phraseList));
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            return handleError(e, Category.AUDIOBOOK, language);
        }
    }

    @PostMapping("/conversation")
    public ResponseEntity<TranscriptionResponseDto> analyzeConversation(
            @RequestParam("audioFile") MultipartFile audioFile,
            @RequestParam("language") Language language,
            @RequestParam("keycloakId") String keycloakId,
            @RequestParam("title") String title,
            @RequestParam(value = "temperature", defaultValue = "0.0") Double temperature,
            @RequestParam(value = "diarization", defaultValue = "false") Boolean diarization,
            @RequestParam(value = "phraseList", required = false) List<String> phraseList
    ) {
        try {
            TranscriptionResponseDto response = conversationTranscriptionService.analyzeConversation(
                    audioFile, language, keycloakId, title, temperature, diarization, safeList(phraseList));
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            return handleError(e, Category.CONVERSATION, language);
        }
    }
}