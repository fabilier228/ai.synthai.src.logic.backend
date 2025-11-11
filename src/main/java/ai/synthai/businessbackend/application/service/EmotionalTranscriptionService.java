package ai.synthai.businessbackend.application.service;

import ai.synthai.businessbackend.domain.model.analysis.EmotionalTranscriptionAnalysis;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

@Service
public class EmotionalTranscriptionService {
    public EmotionalTranscriptionAnalysis analyzeEmotion(MultipartFile audioFile) {
        // TODO: implement real emotion analysis logic
        return EmotionalTranscriptionAnalysis.builder()
            .transcription("dummy transcription")
            .emotion("happy")
            .build();
    }
}
