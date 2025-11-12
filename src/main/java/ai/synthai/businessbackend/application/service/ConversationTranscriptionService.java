package ai.synthai.businessbackend.application.service;

import ai.synthai.businessbackend.application.dto.TranscriptionResponseDto;
import ai.synthai.businessbackend.domain.TranscriptionUtils;
import ai.synthai.businessbackend.domain.model.Category;
import ai.synthai.businessbackend.domain.model.Language;
import ai.synthai.businessbackend.domain.model.Status;
import ai.synthai.businessbackend.domain.model.Transcription;
import ai.synthai.businessbackend.domain.model.analysis.ConversationTranscriptionAnalysis;
import ai.synthai.businessbackend.domain.model.analysis.summary.ConversationSummary;
import ai.synthai.businessbackend.domain.port.outbound.TranscriptionRespositoryPort;
import ai.synthai.businessbackend.infrastructure.client.BatchTranscription;
import ai.synthai.businessbackend.infrastructure.client.openai.ClientOpenAI;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import lombok.val;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.time.LocalDateTime;

@Slf4j
@Service
@RequiredArgsConstructor
public class ConversationTranscriptionService {
    private final BatchTranscription batchTranscription;
    private final ClientOpenAI clientOpenAI;
    private final TranscriptionRespositoryPort transcriptionRepositoryPort;

    public TranscriptionResponseDto analyzeConversation(MultipartFile audioFile, Language language, String keycloakId, String title) {
        try {
            val transcription = batchTranscription.transcribeAudio(audioFile, Category.CONVERSATION);
            val dialogue = TranscriptionUtils.createReadableDialogue(transcription);
            val analysis = clientOpenAI.getTranscriptionAnalysis(Category.CONVERSATION, dialogue, ConversationSummary.class);
            val readyResponse = buildResponse(analysis, dialogue);

            Transcription transcriptionToSave = Transcription.builder()
                    .keycloakId(keycloakId)
                    .title(title)
                    .category(Category.CONVERSATION.name())
                    .transcript(dialogue)
                    .summary(readyResponse.getSummary().toString())
                    .createdAt(LocalDateTime.now())
                    .build();

            transcriptionRepositoryPort.save(transcriptionToSave);

            return TranscriptionResponseDto.builder()
                    .status(Status.COMPLETED)
                    .transcriptionAnalysis(readyResponse)
                    .category(Category.CONVERSATION)
                    .duration(transcription.getDurationMilliseconds())
                    .language(language)
                    .build();
        } catch (Exception e) {
            return TranscriptionResponseDto.builder()
                    .status(Status.FAILED)
                    .category(Category.CONVERSATION)
                    .language(language)
                    .build();
        }
    }


    private ConversationTranscriptionAnalysis buildResponse(ConversationSummary analysis, String dialogue) {
        return ConversationTranscriptionAnalysis.builder()
                .transcription(dialogue)
                .summary(analysis)
                .build();
    }
}
