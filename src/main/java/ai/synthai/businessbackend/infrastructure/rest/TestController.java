package ai.synthai.businessbackend.infrastructure.rest;

import ai.synthai.businessbackend.domain.model.Category;
import ai.synthai.businessbackend.infrastructure.client.openai.ClientOpenAI;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;

@Controller
@AllArgsConstructor
public class TestController {

    private  final ClientOpenAI clientOpenAI;

    @PostMapping("/test/analysis")
    public String testAnalysis(@RequestBody String transcription, Category category) {
        return "Test analysis endpoint is working!";
    }

}
