package ai.synthai.businessbackend.infrastructure.rest;


import ai.synthai.businessbackend.application.dto.TranscriptionRequestDto;
import ai.synthai.businessbackend.application.dto.TranscriptionResponseDto;
import ai.synthai.businessbackend.application.service.AudiobookTranscriptionService;
import ai.synthai.businessbackend.application.service.ConversationTranscriptionService;
import ai.synthai.businessbackend.application.service.LectureTranscriptionService;
import ai.synthai.businessbackend.application.service.SongTranscriptionService;
import ai.synthai.businessbackend.domain.model.Category;
import ai.synthai.businessbackend.domain.model.Status;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

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
            @RequestBody TranscriptionRequestDto requestDto) {
        try {
            TranscriptionResponseDto response = songTranscriptionService.analyzeSong(requestDto.audioFile(), requestDto.language(),
                    requestDto.keycloakId(), requestDto.title(), requestDto.temperature(), requestDto.diarization(), requestDto.phraseList());
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            log.error("Error during song analysis", e);
            return ResponseEntity.status(500).body(
                    TranscriptionResponseDto.builder()
                            .status(Status.FAILED)
                            .category(Category.SONG)
                            .language(requestDto.language())
                            .build()
            );
        }
    }


    @PostMapping("/lecture")
    public ResponseEntity<TranscriptionResponseDto> analyzeLecture(
            @RequestBody TranscriptionRequestDto requestDto) {
        try {
            TranscriptionResponseDto response = lectureTranscriptionService.analyzeLecture(requestDto.audioFile(), requestDto.language(),
                    requestDto.keycloakId(), requestDto.title(), requestDto.temperature(), requestDto.diarization(), requestDto.phraseList());
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            log.error("Error during lecture analysis", e);
            return ResponseEntity.status(500).body(
                    TranscriptionResponseDto.builder()
                            .status(Status.FAILED)
                            .category(Category.LECTURE)
                            .language(requestDto.language())
                            .build());
        }
    }

    @PostMapping("/audiobook")
    public ResponseEntity<TranscriptionResponseDto> analyzeAudiobook(
            @RequestBody TranscriptionRequestDto requestDto) {
        try {
            TranscriptionResponseDto response = audiobookTranscriptionService.analyzeAudiobook(requestDto.audioFile(), requestDto.language(),
                    requestDto.keycloakId(), requestDto.title(), requestDto.temperature(), requestDto.diarization(), requestDto.phraseList());
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            log.error("Error during audiobook analysis", e);
            return ResponseEntity.status(500).body(
                    TranscriptionResponseDto
                            .builder()
                            .status(Status.FAILED)
                            .category(Category.AUDIOBOOK)
                            .language(requestDto.language())
                            .build());
        }
    }

    @PostMapping("/conversation")
    public ResponseEntity<TranscriptionResponseDto> analyzeConversation(
            @RequestBody TranscriptionRequestDto requestDto) {
        try {
            TranscriptionResponseDto response = conversationTranscriptionService.analyzeConversation(requestDto.audioFile(), requestDto.language(),
                    requestDto.keycloakId(), requestDto.title(), requestDto.temperature(), requestDto.diarization(), requestDto.phraseList());
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            log.error("Error during call analysis", e);
            return ResponseEntity.status(500).body(
                    TranscriptionResponseDto.builder()
                            .status(Status.FAILED)
                            .category(Category.CONVERSATION)
                            .language(requestDto.language())
                            .build());
        }
    }
}
