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
import ai.synthai.businessbackend.infrastructure.client.EmotionRecognition;
import ai.synthai.businessbackend.infrastructure.client.openai.ClientOpenAI;
import ai.synthai.businessbackend.infrastructure.persistence.TranscriptionMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import lombok.val;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.time.LocalDateTime;
import java.util.List;

@Slf4j
@Service
@RequiredArgsConstructor
public class ConversationTranscriptionService {
    private final BatchTranscription batchTranscription;
    private final ClientOpenAI clientOpenAI;
    private final TranscriptionRespositoryPort transcriptionRepositoryPort;
    private final EmotionRecognition emotionRecognition;

    public TranscriptionResponseDto analyzeConversation(MultipartFile audioFile, Language language, String keycloakId,
                                                        String title, double temperature, boolean diarization, List<String> phraseList) {
        try {
            log.info("Starting conversation analysis for keycloakId={}, title={}, language={}", keycloakId, title, language);
            val transcription = batchTranscription.transcribeAudio(audioFile, diarization, language, phraseList);
            log.info("Transcription result received");
            val dialogue = TranscriptionUtils.createReadableDialogue(transcription);
            log.info("Dialogue created from transcription");
            val detectedEmotion = emotionRecognition.recognizeEmotion(TranscriptionUtils.getText(transcription));
            log.info("Detected emotion: {}", detectedEmotion);
            val analysis = clientOpenAI.getTranscriptionAnalysis(Category.CONVERSATION, dialogue, ConversationSummary.class, temperature);
            log.info("Transcription analysis received from OpenAI");
            val readyResponse = buildResponse(analysis, dialogue, detectedEmotion);

            Transcription transcriptionToSave = Transcription.builder()
                    .keycloakId(keycloakId)
                    .title(title)
                    .category(Category.CONVERSATION.name())
                    .transcript(dialogue)
                    .summary(TranscriptionMapper.summaryToJsonString(readyResponse.getSummary()))
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
            log.error("Error during conversation transcription analysis", e);
            return TranscriptionResponseDto.builder()
                    .status(Status.FAILED)
                    .category(Category.CONVERSATION)
                    .language(language)
                    .build();
        }
    }


    private ConversationTranscriptionAnalysis buildResponse(ConversationSummary analysis, String dialogue, String detectedEmotion) {
        analysis.setEmotions(List.of(detectedEmotion));
        return ConversationTranscriptionAnalysis.builder()
                .transcription(dialogue)
                .summary(analysis)
                .build();
    }
}
