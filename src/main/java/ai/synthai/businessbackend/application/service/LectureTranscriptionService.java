package ai.synthai.businessbackend.application.service;

import ai.synthai.businessbackend.application.dto.TranscriptionResponseDto;
import ai.synthai.businessbackend.application.dto.TranscriptionResultDto;
import ai.synthai.businessbackend.domain.TranscriptionUtils;
import ai.synthai.businessbackend.domain.model.*;
import ai.synthai.businessbackend.domain.model.analysis.LectureTranscriptionAnalysis;
import ai.synthai.businessbackend.domain.model.analysis.summary.LectureSummary;
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
public class LectureTranscriptionService {
    private final BatchTranscription batchTranscription;
    private final ClientOpenAI clientOpenAI;
    private final TranscriptionRespositoryPort transcriptionRepositoryPort;

    public TranscriptionResponseDto analyzeLecture(MultipartFile audioFile, Language language, String keycloakId, String title) {
        try {
            val transcription = batchTranscription.transcribeAudio(audioFile, Category.LECTURE);
            val transcriptionContent = TranscriptionUtils.getText((TranscriptionResultDto) transcription);
            val analysis = clientOpenAI.getTranscriptionAnalysis(Category.LECTURE, transcriptionContent);
            val readyResponse = buildResponse(analysis, transcriptionContent);

            Transcription transcriptionToSave = Transcription.builder()
                    .keycloakId(keycloakId)
                    .title(title)
                    .category(Category.LECTURE.name())
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

    private LectureTranscriptionAnalysis buildResponse(Map<String, Object> analysis, String transcriptionContent) {
        LectureSummary lectureSummary = LectureSummary.builder()
                .title((String) analysis.get("title"))
                .speaker((String) analysis.get("speaker"))
                .language((String) analysis.get("language"))
                .fieldOfStudy((String) analysis.get("fieldOfStudy"))
                .topics((List<String>) analysis.get("topics"))
                .keyConcepts((List<String>) analysis.get("keyConcepts"))
                .tone((String) analysis.get("tone"))
                .structure((List<String>) analysis.get("structure"))
                .targetAudience((String) analysis.get("targetAudience"))
                .summary((String) analysis.get("summary"))
                .keyQuotes((List<String>) analysis.get("keyQuotes"))
                .mainArgument((String) analysis.get("mainArgument"))
                .evidenceAndExamples((List<String>) analysis.get("evidenceAndExamples"))
                .conclusion((String) analysis.get("conclusion"))
                .emotions((List<String>) analysis.get("emotions"))
                .complexityLevel((String) analysis.get("complexityLevel"))
                .purpose((String) analysis.get("purpose"))
                .build();

        return LectureTranscriptionAnalysis.builder()
                .transcription(transcriptionContent)
                .summary(lectureSummary)
                .build();
    }

}
