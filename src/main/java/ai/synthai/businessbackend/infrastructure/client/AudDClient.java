package ai.synthai.businessbackend.infrastructure.client;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.*;
import org.springframework.stereotype.Component;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.multipart.MultipartFile;

import java.util.LinkedHashMap;
import java.util.Map;

@Component
public class AudDClient {
    @Value("${audd.api.key}")
    private String auddApiKey;

    @Value("${audd.api.url}")
    private String auddApiUrl;

    public Map<String, Object> recognizeMusic(MultipartFile audioFile) {
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.MULTIPART_FORM_DATA);

        MultiValueMap<String, Object> body = new LinkedMultiValueMap<>();
        body.add("api_token", auddApiKey);
        body.add("file", audioFile.getResource());
        body.add("return", "apple_music,spotify,deezer");

        HttpEntity<MultiValueMap<String, Object>> requestEntity = new HttpEntity<>(body, headers);
        RestTemplate restTemplate = new RestTemplate();
        @SuppressWarnings("rawtypes")
        ResponseEntity<LinkedHashMap> response = restTemplate.postForEntity(
                auddApiUrl, requestEntity, LinkedHashMap.class
        );
        if (response.getStatusCode().is2xxSuccessful() && response.getBody() != null) {
            Object resultObj = response.getBody().get("result");
            if (resultObj instanceof Map) {
                return (Map<String, Object>) resultObj;
            }
        }
        return null;
    }
}
