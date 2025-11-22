package ai.synthai.businessbackend.application.service;

import ai.synthai.businessbackend.application.dto.MusicRecognitionResultDto;
import ai.synthai.businessbackend.application.dto.TranscriptionResponseDto;
import ai.synthai.businessbackend.application.dto.TranscriptionResultDto;
import ai.synthai.businessbackend.domain.TranscriptionUtils;
import ai.synthai.businessbackend.domain.model.*;
import ai.synthai.businessbackend.domain.model.analysis.SongTranscriptionAnalysis;
import ai.synthai.businessbackend.domain.model.analysis.summary.SongSummary;
import ai.synthai.businessbackend.domain.model.batch.transcription.response.CombinedPhrase;
import ai.synthai.businessbackend.domain.model.batch.transcription.response.Phrase;
import ai.synthai.businessbackend.domain.port.outbound.TranscriptionRespositoryPort;
import ai.synthai.businessbackend.infrastructure.client.AudDClient;
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
import java.util.Map;

@Slf4j
@Service
@RequiredArgsConstructor
public class SongTranscriptionService {
    private final AudDClient audDClient;
    private final BatchTranscription batchTranscription;
    private final ClientOpenAI clientOpenAI;
    private final TranscriptionRespositoryPort transcriptionRepositoryPort;
    private final TranscriptionMapper transcriptionMapper;

    public TranscriptionResponseDto analyzeSong(MultipartFile audioFile, Language language, String keycloakId, String title) {
        try {
            log.info("Starting song analysis for keycloakId={}, title={}, language={}", keycloakId, title, language);
            val musicResult = recognizeMusic(audioFile);
            log.info("Music recognition result: {}", musicResult.getTitle());
            val transcription = batchTranscription.transcribeAudio(audioFile, Category.SONG, language);
            log.info("Transcription result received");
            val dialogue = TranscriptionUtils.createReadableDialogue(transcription);
            log.info("Dialogue created from transcription");
            val analysis = clientOpenAI.getTranscriptionAnalysis(Category.SONG, dialogue, SongSummary.class);
            log.info("Transcription analysis received from OpenAI");
            val readyResponse = buildResponse(analysis, musicResult, dialogue);

            Transcription transcriptionToSave = Transcription.builder()
                    .keycloakId(keycloakId)
                    .title(title)
                    .category(Category.SONG.name())
                    .transcript(dialogue)
                    .summary(TranscriptionMapper.summaryToJsonString(readyResponse.getSummary()))
                    .createdAt(LocalDateTime.now())
                    .build();

            log.info("Saving transcription to repository");
            transcriptionRepositoryPort.save(transcriptionToSave);
            log.info("Transcription saved successfully");

            return TranscriptionResponseDto.builder()
                    .status(Status.COMPLETED)
                    .transcriptionAnalysis(readyResponse)
                    .category(Category.SONG)
                    .duration(transcription.getDurationMilliseconds())
                    .language(language)
                    .build();

        } catch (Exception e) {
            log.error("Error during song transcription analysis", e);
            return TranscriptionResponseDto.builder()
                    .status(Status.FAILED)
                    .category(Category.SONG)
                    .language(language)
                    .build();
        }
    }

    private MusicRecognitionResultDto recognizeMusic(MultipartFile audioFile) {
        try {
            Map<String, Object> resultMap = audDClient.recognizeMusic(audioFile);
            if (resultMap != null) {
                return MusicRecognitionResultDto.builder()
                        .title((String) resultMap.get("title"))
                        .artist((String) resultMap.get("artist"))
                        .album((String) resultMap.get("album"))
                        .label((String) resultMap.get("label"))
                        .releaseDate((String) resultMap.get("release_date"))
                        .duration(resultMap.get("duration") instanceof Number ? ((Number) resultMap.get("duration")).intValue() : null)
                        .genre((String) resultMap.get("genre"))
                        .confidence(resultMap.get("score") instanceof Number ? ((Number) resultMap.get("score")).doubleValue() : null)
                        .recognized(true)
                        .build();
            }
        } catch (Exception e) {
            log.error("Error analyzing song", e);
        }
        return MusicRecognitionResultDto.builder().recognized(false).build();
    }

    private SongTranscriptionAnalysis buildResponse(SongSummary analysis, MusicRecognitionResultDto musicResult, String dialogue) {
        if (!musicResult.isRecognized()) {
            return SongTranscriptionAnalysis.builder()
                    .transcription(dialogue)
                    .summary(analysis)
                    .build();
        }

        analysis.setTitle(musicResult.getTitle());
        analysis.setArtist(musicResult.getArtist());

        return SongTranscriptionAnalysis.builder()
                .transcription(dialogue)
                .summary(analysis)
                .build();
    }

    private TranscriptionResultDto mapToTranscriptionResult(Map<String, Object> map) {
        return TranscriptionResultDto.builder()
                .durationMilliseconds(map.get("durationMilliseconds") != null ? (int) map.get("durationMilliseconds") : 0)
                .phrases(map.get("phrases") != null ? (List<Phrase>) map.get("phrases") : null)
                .combinedPhrases(map.get("combinedPhrases") != null ? (List<CombinedPhrase>) map.get("combinedPhrases") : null)
                .build();
    }


}
