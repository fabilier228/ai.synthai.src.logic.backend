package ai.synthai.businessbackend.application.service;

import ai.synthai.businessbackend.application.dto.TranscriptionResponseDto;
import ai.synthai.businessbackend.application.dto.TranscriptionResultDto;
import ai.synthai.businessbackend.domain.TranscriptionUtils;
import ai.synthai.businessbackend.domain.model.Category;
import ai.synthai.businessbackend.domain.model.Language;
import ai.synthai.businessbackend.domain.model.Status;
import ai.synthai.businessbackend.domain.model.Transcription;
import ai.synthai.businessbackend.domain.model.analysis.CallTranscriptionAnalysis;
import ai.synthai.businessbackend.domain.model.analysis.summary.CallSummary;
import ai.synthai.businessbackend.domain.model.analysis.summary.SongSummary;
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
public class CallTranscriptionService {
    private final BatchTranscription batchTranscription;
    private final ClientOpenAI clientOpenAI;
    private final TranscriptionRespositoryPort transcriptionRepositoryPort;

    public TranscriptionResponseDto analyzeCall(MultipartFile audioFile, Language language, String keycloakId, String title) {
        try {
            val transcription = batchTranscription.transcribeAudio(audioFile, Category.PHONE_CALL);
            val dialogue = TranscriptionUtils.createReadableDialogue((TranscriptionResultDto) transcription);
            val analysis = (SongSummary) clientOpenAI.getTranscriptionAnalysis(Category.PHONE_CALL, dialogue);
            val readyResponse = buildResponse((Map<String, Object>) analysis, dialogue);

            Transcription transcriptionToSave = Transcription.builder()
                    .keycloakId(keycloakId)
                    .title(title)
                    .category(Category.PHONE_CALL.name())
                    .transcript(dialogue)
                    .summary(readyResponse.getSummary().toString())
                    .createdAt(LocalDateTime.now())
                    .build();

            transcriptionRepositoryPort.save(transcriptionToSave);

            return TranscriptionResponseDto.builder()
                    .status(Status.COMPLETED)
                    .transcriptionAnalysis(readyResponse)
                    .category(Category.PHONE_CALL)
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


    private CallTranscriptionAnalysis buildResponse(Map<String, Object> analysis, String dialogue) {
        CallSummary callSummary = CallSummary.builder()
                .participants((List<String>) analysis.get("participants"))
                .language((String) analysis.get("language"))
                .relationship((String) analysis.get("relationship"))
                .context((String) analysis.get("context"))
                .topics((List<String>) analysis.get("topics"))
                .tone((String) analysis.get("tone"))
                .summary((String) analysis.get("summary"))
                .emotions((List<String>) analysis.get("emotions"))
                .conflictLevel((String) analysis.get("conflictLevel"))
                .agreementOutcome((String) analysis.get("agreementOutcome"))
                .keyQuotes((List<String>) analysis.get("keyQuotes"))
                .build();

        return CallTranscriptionAnalysis.builder()
                .transcription(dialogue)
                .summary(callSummary)
                .build();
    }
}
