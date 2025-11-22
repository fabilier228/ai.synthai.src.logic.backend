package ai.synthai.businessbackend.application.service;


import ai.synthai.businessbackend.application.dto.TranscriptionResponseDto;
import ai.synthai.businessbackend.domain.TranscriptionUtils;
import ai.synthai.businessbackend.domain.model.Category;
import ai.synthai.businessbackend.domain.model.Language;
import ai.synthai.businessbackend.domain.model.Status;
import ai.synthai.businessbackend.domain.model.Transcription;
import ai.synthai.businessbackend.domain.model.analysis.AudiobookTranscriptionAnalysis;
import ai.synthai.businessbackend.domain.model.analysis.summary.AudiobookSummary;
import ai.synthai.businessbackend.domain.port.outbound.TranscriptionRespositoryPort;
import ai.synthai.businessbackend.infrastructure.client.BatchTranscription;
import ai.synthai.businessbackend.infrastructure.client.openai.ClientOpenAI;
import ai.synthai.businessbackend.infrastructure.persistence.TranscriptionMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import lombok.val;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.time.LocalDateTime;

@Slf4j
@Service
@RequiredArgsConstructor
public class AudiobookTranscriptionService {
    private final BatchTranscription batchTranscription;
    private final ClientOpenAI clientOpenAI;
    private final TranscriptionRespositoryPort transcriptionRepositoryPort;

    public TranscriptionResponseDto analyzeAudiobook(MultipartFile audioFile, Language language, String keycloakId, String title) {
        try {
            log.info("Starting audiobook analysis for keycloakId={}, title={}, language={}", keycloakId, title, language);
            val transcription = batchTranscription.transcribeAudio(audioFile, Category.AUDIOBOOK, language);
            log.info("Transcription result received");
            val transcriptionContent = TranscriptionUtils.getText(transcription);
            log.info("Transcription content extracted");
            val analysis = clientOpenAI.getTranscriptionAnalysis(Category.AUDIOBOOK, transcriptionContent, AudiobookSummary.class);
            log.info("Transcription analysis received from OpenAI");
            val readyResponse = buildResponse(analysis, transcriptionContent);

            Transcription transcriptionToSave = Transcription.builder()
                    .keycloakId(keycloakId)
                    .title(title)
                    .category(Category.AUDIOBOOK.name())
                    .transcript(transcriptionContent)
                    .summary(TranscriptionMapper.summaryToJsonString(readyResponse.getSummary()))
                    .createdAt(LocalDateTime.now())
                    .build();

            transcriptionRepositoryPort.save(transcriptionToSave);

            return TranscriptionResponseDto.builder()
                    .status(Status.COMPLETED)
                    .transcriptionAnalysis(readyResponse)
                    .category(Category.AUDIOBOOK)
                    .duration(transcription.getDurationMilliseconds())
                    .language(language)
                    .build();
        } catch (Exception e) {
            log.error("Error during song transcription analysis", e);
            return TranscriptionResponseDto.builder()
                    .status(Status.FAILED)
                    .category(Category.AUDIOBOOK)
                    .language(language)
                    .build();
        }
    }

    private AudiobookTranscriptionAnalysis buildResponse(AudiobookSummary analysis, String transcriptionContent) {
        return AudiobookTranscriptionAnalysis.builder()
                .transcription(transcriptionContent)
                .summary(analysis)
                .build();
    }
}
