package ai.synthai.businessbackend.application.service;

import ai.synthai.businessbackend.application.dto.TranscriptionResponseDto;
import ai.synthai.businessbackend.domain.TranscriptionUtils;
import ai.synthai.businessbackend.domain.model.*;
import ai.synthai.businessbackend.domain.model.analysis.LectureTranscriptionAnalysis;
import ai.synthai.businessbackend.domain.model.analysis.summary.LectureSummary;
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
import java.util.List;

@Slf4j
@Service
@RequiredArgsConstructor
public class LectureTranscriptionService {
    private final BatchTranscription batchTranscription;
    private final ClientOpenAI clientOpenAI;
    private final TranscriptionRespositoryPort transcriptionRepositoryPort;

    public TranscriptionResponseDto analyzeLecture(MultipartFile audioFile, Language language, String keycloakId,
                                                   String title, double temperature, boolean diarization, List<String> phraseList) {
        try {
            log.info("Starting lecture analysis for keycloakId={}, title={}, language={}", keycloakId, title, language);
            val transcription = batchTranscription.transcribeAudio(audioFile, diarization, language, phraseList);
            log.info("Transcription result received");
            val transcriptionContent = TranscriptionUtils.getText(transcription);
            log.info("Transcription content extracted");
            val analysis = clientOpenAI.getTranscriptionAnalysis(Category.LECTURE, transcriptionContent, LectureSummary.class, temperature);
            log.info("Transcription analysis received from OpenAI");
            val readyResponse = buildResponse(analysis, transcriptionContent);

            Transcription transcriptionToSave = Transcription.builder()
                    .keycloakId(keycloakId)
                    .title(title)
                    .category(Category.LECTURE.name())
                    .transcript(transcriptionContent)
                    .summary(TranscriptionMapper.summaryToJsonString(readyResponse.getSummary()))
                    .createdAt(LocalDateTime.now())
                    .build();

            transcriptionRepositoryPort.save(transcriptionToSave);

            return TranscriptionResponseDto.builder()
                    .status(Status.COMPLETED)
                    .transcriptionAnalysis(readyResponse)
                    .category(Category.LECTURE)
                    .duration(transcription.getDurationMilliseconds())
                    .language(language)
                    .build();
        } catch (Exception e) {
            log.error("Error during song transcription analysis", e);
            return TranscriptionResponseDto.builder()
                    .status(Status.FAILED)
                    .category(Category.LECTURE)
                    .language(language)
                    .build();
        }
    }

    private LectureTranscriptionAnalysis buildResponse(LectureSummary analysis, String transcriptionContent) {
        return LectureTranscriptionAnalysis.builder()
                .transcription(transcriptionContent)
                .summary(analysis)
                .build();
    }

}
