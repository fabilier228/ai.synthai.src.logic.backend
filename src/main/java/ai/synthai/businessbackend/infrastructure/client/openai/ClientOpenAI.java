package ai.synthai.businessbackend.infrastructure.client.openai;

import ai.synthai.businessbackend.domain.model.Category;
import com.azure.ai.openai.OpenAIClient;
import com.azure.ai.openai.OpenAIClientBuilder;
import com.azure.ai.openai.models.ChatCompletions;
import com.azure.ai.openai.models.ChatCompletionsOptions;
import com.azure.ai.openai.models.ChatRequestMessage;
import com.azure.ai.openai.models.ChatRequestUserMessage;
import com.azure.core.credential.AzureKeyCredential;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import lombok.val;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Map;
@Component
@RequiredArgsConstructor
public class ClientOpenAI {

    @Value("${spring.azure.resources.openai.key}")
    private String apiKey;

    @Value("${spring.azure.resources.openai.endpoint}")
    private String apiEndpoint;

    @Value("${spring.azure.resources.openai.deployment-name}")
    private String deploymentName;

    private final PromptTemplateProvider promptTemplateProvider;

    public Map<String, Object> getTranscriptionAnalysis(Category category, String transcript) {
        OpenAIClient client = createClient();

        String promptTemplate = promptTemplateProvider.templateByCategory(category, transcript);

        List<ChatRequestMessage> messages = buildChatMessage(promptTemplate);

        ChatCompletionsOptions options = new ChatCompletionsOptions(messages);

        ChatCompletions chatCompletions = client.getChatCompletions(deploymentName, options);

        val content = chatCompletions
                .getChoices()
                .get(0)
                .getMessage()
                .getContent();
        ObjectMapper objectMapper = new ObjectMapper();
        try {
            return objectMapper.readValue(content, Map.class);
        } catch (Exception e) {
            throw new RuntimeException("Failed to parse LLM response as JSON: " + content, e);
        }
    }

    private OpenAIClient createClient() {
        return new OpenAIClientBuilder()
                .credential(new AzureKeyCredential(apiKey))
                .endpoint(apiEndpoint)
                .buildClient();
    }

    private List<ChatRequestMessage> buildChatMessage(String prompt) {
        return List.of(new ChatRequestUserMessage(prompt));
    }
}
