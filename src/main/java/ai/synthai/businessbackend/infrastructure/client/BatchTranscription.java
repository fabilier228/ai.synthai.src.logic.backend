package ai.synthai.businessbackend.infrastructure.client;


import ai.synthai.businessbackend.application.dto.TranscriptionResultDto;
import ai.synthai.businessbackend.domain.model.Category;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.FileSystemResource;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.client.RestTemplate;

import java.io.File;

@Component
@RequiredArgsConstructor
public class BatchTranscription {
    private static final String DEFAULT_LOCALE = "en-US,pl-PL";
    private static final String TEMPLATE = "{\"locales\":[\"%s\"], \"diarizationEnabled\":%b}";

    private final RestTemplate restTemplate;

    @Value("${spring.azure.resources.speech.key}")
    private String apiKey;

    @Value("${spring.azure.resources.speech.region}")
    private String region;

    public ResponseEntity<TranscriptionResultDto> transcribeAudio(File audioFile, String locale, Category category) {

        try {
            HttpHeaders headers = new HttpHeaders();
            headers.setContentType(MediaType.MULTIPART_FORM_DATA);
            headers.set("Ocp-Apim-Subscription-Key", apiKey);

            MultiValueMap<String, Object> body = new LinkedMultiValueMap<>();

            body.add("audio", new FileSystemResource(audioFile));

            String definitionJson = createLocales(category);

            HttpHeaders definitionHeaders = new HttpHeaders();
            definitionHeaders.setContentType(MediaType.APPLICATION_JSON);

            HttpEntity<String> definitionPart = new HttpEntity<>(definitionJson, definitionHeaders);
            body.add("definition", definitionPart);

            HttpEntity<MultiValueMap<String, Object>> requestEntity = new HttpEntity<>(body, headers);

            String fullUrl = createApiEndpoint();

            return restTemplate.postForEntity(fullUrl, requestEntity, TranscriptionResultDto.class);
        } catch (Exception e) {
            throw new RuntimeException("Error during transcription request", e);
        }
    }

    private String createApiEndpoint() {
        return String.format("https://%s.api.cognitive.microsoft.com/speechtotext/transcriptions:transcribe?api-version=2025-10-15'", region);
    }

    public String createLocales(Category category) {
        boolean diarization = switch (category) {
            case LECTURE, PHONE_CALL -> true;
            case AUDIOBOOK, SONG -> false;
        };

        return String.format(TEMPLATE, DEFAULT_LOCALE, diarization);
    }


}
