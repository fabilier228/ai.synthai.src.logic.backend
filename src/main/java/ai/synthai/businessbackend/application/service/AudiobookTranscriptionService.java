package ai.synthai.businessbackend.application.service;


import ai.synthai.businessbackend.application.dto.TranscriptionResponseDto;
import ai.synthai.businessbackend.application.dto.TranscriptionResultDto;
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
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import lombok.val;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;

@Slf4j
@Service
@RequiredArgsConstructor
public class AudiobookTranscriptionService {
    private final BatchTranscription batchTranscription;
    private final ClientOpenAI clientOpenAI;
    private final TranscriptionRespositoryPort transcriptionRepositoryPort;

    public TranscriptionResponseDto analyzeAudiobook(MultipartFile audioFile, Language language, String keycloakId, String title) {
        try {
            val transcription = batchTranscription.transcribeAudio(audioFile, Category.AUDIOBOOK);
            val transcriptionContent = TranscriptionUtils.getText((TranscriptionResultDto) transcription);
            val analysis = clientOpenAI.getTranscriptionAnalysis(Category.AUDIOBOOK, transcriptionContent);
            val readyResponse = buildResponse(analysis, transcriptionContent);

            Transcription transcriptionToSave = Transcription.builder()
                    .keycloakId(keycloakId)
                    .title(title)
                    .category(Category.AUDIOBOOK.name())
                    .transcript(transcriptionContent)
                    .summary(readyResponse.getSummary().toString())
                    .createdAt(LocalDateTime.now())
                    .build();

            transcriptionRepositoryPort.save(transcriptionToSave);

            return TranscriptionResponseDto.builder()
                    .status(Status.COMPLETED)
                    .transcriptionAnalysis(readyResponse)
                    .category(Category.LECTURE)
                    .duration(transcription.get("duration") instanceof Number ? ((Number) transcription.get("duration")).floatValue() : null)
                    .language(language)
                    .build();
        } catch (Exception e) {
            return TranscriptionResponseDto.builder()
                    .status(Status.FAILED)
                    .category(Category.SONG)
                    .language(language)
                    .build();
        }
    }

    private AudiobookTranscriptionAnalysis buildResponse(Map<String, Object> analysis, String transcriptionContent) {
        AudiobookSummary audiobookSummary = AudiobookSummary.builder()
                .title((String) analysis.get("title"))
                .author((String) analysis.get("author"))
                .narrator((String) analysis.get("narrator"))
                .language((String) analysis.get("language"))
                .genre((String) analysis.get("genre"))
                .subGenres((List<String>) analysis.get("subGenres"))
                .themes((List<String>) analysis.get("themes"))
                .tone((String) analysis.get("tone"))
                .narrativeStyle((String) analysis.get("narrativeStyle"))
                .setting((String) analysis.get("setting"))
                .mainCharacters((List<String>) analysis.get("mainCharacters"))
                .plotSummary((String) analysis.get("plotSummary"))
                .keyMoments((List<String>) analysis.get("keyMoments"))
                .emotions((List<String>) analysis.get("emotions"))
                .symbolism((List<String>) analysis.get("symbolism"))
                .pacing((String) analysis.get("pacing"))
                .audioStyle((String) analysis.get("audioStyle"))
                .soundDesign((String) analysis.get("soundDesign"))
                .targetAudience((String) analysis.get("targetAudience"))
                .lengthMinutes(analysis.get("lengthMinutes") != null ? ((Number) analysis.get("lengthMinutes")).intValue() : null)
                .purpose((String) analysis.get("purpose"))
                .complexityLevel((String) analysis.get("complexityLevel"))
                .moodShifts((List<String>) analysis.get("moodShifts"))
                .narrativeArc((List<String>) analysis.get("narrativeArc"))
                .build();

        return AudiobookTranscriptionAnalysis.builder()
                .transcription(transcriptionContent)
                .summary(audiobookSummary)
                .build();
    }
}
