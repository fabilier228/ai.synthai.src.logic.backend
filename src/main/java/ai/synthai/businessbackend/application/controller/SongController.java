package ai.synthai.businessbackend.application.controller;

import ai.synthai.businessbackend.domain.model.MusicRecognitionResult;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpEntity;

import java.util.LinkedHashMap;
import java.util.Map;

@RestController
@RequestMapping("/songs")
@RequiredArgsConstructor
@Slf4j
public class SongController {

    @Value("${AUDD_API_KEY}")
    private String auddApiKey;

    @Value("${AUDD_API_URL}")
    private String auddApiUrl;

    @PostMapping(value = "/analyze", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<MusicRecognitionResult> analyzeSong(@RequestParam("audioFile") MultipartFile audioFile) {
        try {
            log.info("Received song analysis request for file: {}", audioFile.getOriginalFilename());
            if (audioFile.isEmpty()) {
                return ResponseEntity.badRequest().build();
            }

            MusicRecognitionResult result = recognizeMusicWithAudD(audioFile);
            return ResponseEntity.ok(result);
        } catch (Exception e) {
            log.error("Error analyzing song", e);
            return ResponseEntity.internalServerError().build();
        }
    }

    private MusicRecognitionResult recognizeMusicWithAudD(MultipartFile audioFile) {
        try {
            HttpHeaders headers = new HttpHeaders();
            headers.setContentType(MediaType.MULTIPART_FORM_DATA);

            MultiValueMap<String, Object> body = new LinkedMultiValueMap<>();
            body.add("api_token", auddApiKey);
            body.add("file", audioFile.getResource());
            body.add("return", "apple_music,spotify,deezer");

            HttpEntity<MultiValueMap<String, Object>> requestEntity = new HttpEntity<>(body, headers);

            RestTemplate restTemplate = new RestTemplate();
            ResponseEntity<LinkedHashMap<String, Object>> response = restTemplate.postForEntity(
                    auddApiUrl, requestEntity, (Class<LinkedHashMap<String, Object>>) (Class<?>) LinkedHashMap.class
            );

            if (response.getStatusCode().is2xxSuccessful() && response.getBody() != null) {
                Object resultObj = response.getBody().get("result");
                if (resultObj instanceof Map) {
                    Map<String, Object> resultMap = (Map<String, Object>) resultObj;
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
            }
        } catch (Exception e) {
            log.error("Error calling AudD.io API", e);
        }
        return MusicRecognitionResult.builder().recognized(false).build();
    }

    @GetMapping("/test")
    public ResponseEntity<String> test() {
        return ResponseEntity.ok("Song analysis service is running!");
    }
}
