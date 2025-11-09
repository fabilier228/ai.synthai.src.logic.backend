package ai.synthai.businessbackend.infrastructure.client.openai;

import ai.synthai.businessbackend.domain.model.Category;
import com.azure.ai.openai.OpenAIClient;
import com.azure.ai.openai.OpenAIClientBuilder;
import com.azure.ai.openai.models.ChatCompletions;
import com.azure.ai.openai.models.ChatCompletionsOptions;
import com.azure.ai.openai.models.ChatRequestMessage;
import com.azure.ai.openai.models.ChatRequestUserMessage;
import com.azure.core.credential.AzureKeyCredential;
import lombok.AllArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
public class ClientOpenAI {

    @Value("${spring.azure.resources.api.key}")
    private String apiKey;

    @Value("${spring.azure.resources.api.endpoint}")
    private String apiEndpoint;

    @Value("${spring.azure.resources.openai.deployment-name}")
    private String deploymentName;

    private PromptTemplateProvider promptTemplateProvider;

    public String getTranscriptionAnalysis(Category category, String transcript) {
        OpenAIClient client = createClient();

        String promptTemplate = promptTemplateProvider.templateByCategory(category, transcript);

        List<ChatRequestMessage> messages = buildChatMessage(promptTemplate);

        ChatCompletionsOptions options = new ChatCompletionsOptions(messages);

        ChatCompletions chatCompletions = client.getChatCompletions(deploymentName, options);

        return chatCompletions
                .getChoices()
                .get(0)
                .getMessage()
                .getContent();
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
