package ai.synthai.businessbackend.infrastructure.client;


import ai.synthai.businessbackend.application.dto.TranscriptionResultDto;
import ai.synthai.businessbackend.domain.model.Language;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.multipart.MultipartFile;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Slf4j
@Component
@RequiredArgsConstructor
public class BatchTranscription {
    private final RestTemplate restTemplate;

    @Value("${spring.azure.resources.speech.key}")
    private String apiKey;

    @Value("${spring.azure.resources.speech.region}")
    private String region;

    public TranscriptionResultDto transcribeAudio(MultipartFile audioFile, boolean diarization, Language language, List<String> phraseList) {
        try {
            HttpHeaders headers = new HttpHeaders();
            headers.setContentType(MediaType.MULTIPART_FORM_DATA);
            headers.set("Ocp-Apim-Subscription-Key", apiKey);

            MultiValueMap<String, Object> body = new LinkedMultiValueMap<>();

            String definitionJson = createLocales(diarization, language, phraseList);

            HttpHeaders definitionHeaders = new HttpHeaders();
            definitionHeaders.setContentType(MediaType.APPLICATION_JSON_UTF8);

            HttpEntity<String> definitionPart = new HttpEntity<>(definitionJson, definitionHeaders);

            body.add("definition", definitionPart);

            body.add("audio", audioFile.getResource());

            HttpEntity<MultiValueMap<String, Object>> requestEntity = new HttpEntity<>(body, headers);

            String fullUrl = createApiEndpoint();

            log.info("Wysyłanie definition JSON: {}", definitionJson);

            ResponseEntity<TranscriptionResultDto> response = restTemplate.postForEntity(fullUrl, requestEntity, TranscriptionResultDto.class);

            if (response.getStatusCode().is2xxSuccessful()) {
                return response.getBody();
            } else {
                throw new RuntimeException("Speech API error: " + response.getStatusCode());
            }
        } catch (Exception e) {
            throw new RuntimeException("Error during transcription request", e);
        }
    }

    private String createApiEndpoint() {
        return String.format("https://%s.api.cognitive.microsoft.com/speechtotext/transcriptions:transcribe?api-version=2025-10-15", region);
    }

    public String createLocales(Boolean isDiarizationEnabled, Language language, List<String> phraseList) {

        String[] localesArray;
        if (language.equals(Language.POLISH)) {
            localesArray = new String[]{"pl-PL"};
        } else {
            localesArray = new String[]{"en-US"};
        }

        Map<String, Object> definitionMap = new HashMap<>();
        definitionMap.put("locales", localesArray);

        if (isDiarizationEnabled) {
            Map<String, Object> diarizationSettings = new HashMap<>();
            diarizationSettings.put("enabled", true);

            definitionMap.put("diarization", diarizationSettings);
        }

        // if (phraseList != null && !phraseList.isEmpty()) {
        //     Map<String, Object> phraseListObj = new HashMap<>();
        //     phraseListObj.put("phrases", phraseList);
        //     definitionMap.put("phraseList", phraseListObj);
        // }

        try {
            return new ObjectMapper().writeValueAsString(definitionMap);
        } catch (Exception e) {
            throw new RuntimeException("Error creating locales JSON", e);
        }
    }


}
