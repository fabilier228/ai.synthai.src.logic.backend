package ai.synthai.businessbackend.application.service;

import ai.synthai.businessbackend.application.dto.MusicRecognitionResultDto;
import ai.synthai.businessbackend.application.dto.TranscriptionResponseDto;
import ai.synthai.businessbackend.application.dto.TranscriptionResultDto;
import ai.synthai.businessbackend.domain.TranscriptionUtils;
import ai.synthai.businessbackend.domain.model.*;
import ai.synthai.businessbackend.domain.model.analysis.SongTranscriptionAnalysis;
import ai.synthai.businessbackend.domain.model.analysis.summary.SongSummary;
import ai.synthai.businessbackend.domain.port.outbound.TranscriptionRespositoryPort;
import ai.synthai.businessbackend.infrastructure.client.AudDClient;
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
public class SongTranscriptionService {
    private final AudDClient audDClient;
    private final BatchTranscription batchTranscription;
    private final ClientOpenAI clientOpenAI;
    private final TranscriptionRespositoryPort transcriptionRepositoryPort;

    public TranscriptionResponseDto analyzeSong(MultipartFile audioFile, Language language, String keycloakId) {
        try {
            val musicResult = recognizeMusic(audioFile);
            val transcription = batchTranscription.transcribeAudio(audioFile, Category.SONG);
            val dialogue = TranscriptionUtils.createReadableDialogue((TranscriptionResultDto) transcription);
            val analysis = (SongSummary) clientOpenAI.getTranscriptionAnalysis(Category.SONG, dialogue);
            val readyResponse = buildResponse((Map<String, Object>) analysis, musicResult, dialogue);

            Transcription transcriptionToSave = Transcription.builder()
                    .keycloakId(keycloakId)
                    .title(musicResult.isRecognized() ? musicResult.getTitle() : "Unknown")
                    .category(Category.SONG.name())
                    .transcript(dialogue)
                    .summary(readyResponse.getSummary().toString())
                    .createdAt(LocalDateTime.now())
                    .build();

            transcriptionRepositoryPort.save(transcriptionToSave);

            return TranscriptionResponseDto.builder()
                    .status(Status.COMPLETED)
                    .transcriptionAnalysis(readyResponse)
                    .category(Category.SONG)
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

    private SongTranscriptionAnalysis buildResponse(Map<String, Object> analysis, MusicRecognitionResultDto musicResult, String dialogue) {
        if (!musicResult.isRecognized()) {
            return SongTranscriptionAnalysis.builder()
                    .transcription(dialogue)
                    .summary((SongSummary) analysis)
                    .build();
        }

        SongSummary songSummary = SongSummary.builder()
                .title(musicResult.getTitle())
                .artist(musicResult.getArtist())
                .language(analysis.get("language") != null ? analysis.get("language").toString() : null)
                .genre(musicResult.getGenre())
                .genres(analysis.get("genres") != null ? (List<String>) analysis.get("genres") : null)
                .tone(analysis.get("tone") != null ? analysis.get("tone").toString() : null)
                .perspective(analysis.get("perspective") != null ? analysis.get("perspective").toString() : null)
                .adressee(analysis.get("adressee") != null ? analysis.get("adressee").toString() : null)
                .interpretation(analysis.get("interpretation") != null ? analysis.get("interpretation").toString() : null)
                .emotions(analysis.get("emotions") != null ? (List<String>) analysis.get("emotions") : null)
                .symbolism(analysis.get("symbolism") != null ? (List<String>) analysis.get("symbolism") : null)
                .build();

        return SongTranscriptionAnalysis.builder()
                .transcription(dialogue)
                .summary(songSummary)
                .build();
    }


}
