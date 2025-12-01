package ai.synthai.businessbackend.infrastructure.client;

import ai.synthai.businessbackend.application.dto.DeepgramResponseDto;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.RestTemplate;

@Slf4j
@Component
@RequiredArgsConstructor
public class AudioLanguageDetection {

    private final RestTemplate restTemplate;

    @Value("${spring.deepgram.api.key}")
    private String apiKey;

    @Value("${spring.deepgram.language-detection.url}")
    private String serviceUrl;

    public String detectLanguage(byte[] audioFile) {
        try {
            HttpHeaders headers = new HttpHeaders();
            headers.setContentType(MediaType.valueOf("audio/wav"));
            headers.set("Authorization", "Token " + apiKey);

            HttpEntity<byte[]> requestEntity = new HttpEntity<>(audioFile, headers);

            ResponseEntity<DeepgramResponseDto> response = restTemplate.postForEntity(
                    serviceUrl,
                    requestEntity,
                    DeepgramResponseDto.class
            );

            if (response.getStatusCode().is2xxSuccessful()) {
                response.getBody();
                var results = response.getBody().results();
                if (results != null && !results.channels().isEmpty()) {
                    return results.channels().get(0).detected_language();
                }
            }

            return null;

        } catch (HttpClientErrorException e) {
            log.error("Deepgram API Error: {} - {}", e.getStatusCode(), e.getResponseBodyAsString());
            return null;
        } catch (Exception e) {
            log.error("Error during language detection", e);
            return null;
        }
    }
}