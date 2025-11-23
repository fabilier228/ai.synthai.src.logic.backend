package ai.synthai.businessbackend.infrastructure.client;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;

import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.Map;

@Component
public class EmotionRecognition {

    @Value("${emotion.model.port}")
    private String port;

    @Value("${emotion.model.host}")
    private String host;

    @Value("${emotion.model.endpoint}")
    private String endpoint;

    public String recognizeEmotion(String transcription) {
        HttpHeaders headers = new HttpHeaders();
        headers.set("Content-Type", "application/json");


        Map<String, Object> payload = new HashMap<>();
        payload.put("text", transcription);
        payload.put("return_probs", true);
        payload.put("threshold", 0.7);

        HttpEntity<Map<String, Object>> requestEntity = new HttpEntity<>(payload, headers);
        RestTemplate restTemplate = new RestTemplate();

        ResponseEntity<LinkedHashMap> response = restTemplate.postForEntity(
                getServiceUrl(), requestEntity, LinkedHashMap.class
        );

        if (response.getStatusCode().is2xxSuccessful()) {
            return response.getBody().get("label").toString();
        }
        return null;
    }

    private String getServiceUrl() {
        return "http://" + host + ":" + port + endpoint;
    }
}
