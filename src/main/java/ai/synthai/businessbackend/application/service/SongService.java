package ai.synthai.businessbackend.application.service;

import ai.synthai.businessbackend.domain.model.MusicRecognitionResult;
import ai.synthai.businessbackend.infrastructure.client.AudDClient;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.util.Map;

@Service
@RequiredArgsConstructor
@Slf4j
public class SongService {
    private final AudDClient audDClient;

    public MusicRecognitionResult analyzeSong(MultipartFile audioFile) {
        try {
            Map<String, Object> resultMap = audDClient.recognizeMusic(audioFile);
            if (resultMap != null) {
                return MusicRecognitionResult.builder()
                        .title((String) resultMap.get("title"))
                        .artist((String) resultMap.get("artist"))
                        .album((String) resultMap.get("album"))
                        .label((String) resultMap.get("label"))
                        .releaseDate((String) resultMap.get("release_date"))
                        .duration(resultMap.get("duration") instanceof Number ? ((Number) resultMap.get("duration")).intValue() : null)
                        .genre((String) resultMap.get("genre"))
                        .confidence(resultMap.get("score") instanceof Number ? ((Number) resultMap.get("score")).doubleValue() : null)
                        .recognized(true)
                        .build();
            }
        } catch (Exception e) {
            log.error("Error analyzing song", e);
        }
        return MusicRecognitionResult.builder().recognized(false).build();
    }
}
