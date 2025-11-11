package ai.synthai.businessbackend.infrastructure.rest;


import ai.synthai.businessbackend.application.dto.EmotionalTranscriptionResponseDto;
import ai.synthai.businessbackend.application.service.EmotionalTranscriptionService;
import ai.synthai.businessbackend.domain.model.analysis.EmotionalTranscriptionAnalysis;
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
    private final EmotionalTranscriptionService emotionalTranscriptionService;

    @PostMapping("/emotion")
    public ResponseEntity<EmotionalTranscriptionResponseDto> analyzeEmotion(
            @RequestParam("audioFile") MultipartFile audioFile) {
        EmotionalTranscriptionAnalysis analysis = emotionalTranscriptionService.analyzeEmotion(audioFile);
        EmotionalTranscriptionResponseDto dto = EmotionalTranscriptionResponseDto.builder()
            .transcription(analysis.getTranscription())
            .emotion(analysis.getEmotion())
            .build();
        return ResponseEntity.ok(dto);
    }
}
