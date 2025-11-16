package ai.synthai.businessbackend.infrastructure.rest;

import ai.synthai.businessbackend.application.dto.DeleteTranscriptionResponseDto;
import ai.synthai.businessbackend.application.dto.KeycloakIdTranscriptionsDto;
import ai.synthai.businessbackend.domain.model.Status;
import ai.synthai.businessbackend.domain.port.outbound.TranscriptionRespositoryPort;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import lombok.val;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/transcriptions")
@RequiredArgsConstructor
@Slf4j
public class TranscriptionController {
    private final TranscriptionRespositoryPort transcriptionRespositoryPort;

    @GetMapping("/{keycloakId} ")
    public ResponseEntity<KeycloakIdTranscriptionsDto> getTranscriptionsByKeycloakId(@PathVariable String keycloakId) {
        try {
            val entities = transcriptionRespositoryPort.findByKeycloakId(keycloakId);
            return ResponseEntity.status(200).body(
                    KeycloakIdTranscriptionsDto.builder()
                    .status(Status.SUCCESS)
                    .transcriptions(entities)
                    .build());
        } catch (Exception e) {
            log.error("Error fetching transcriptions for keycloakId {}: {}", keycloakId, e.getMessage());
            return ResponseEntity.status(500).body(
                    KeycloakIdTranscriptionsDto.builder()
                    .status(Status.FAILED)
                    .transcriptions(null)
                    .build());
        }
    }

    @DeleteMapping("/{transcriptionId}")
    public ResponseEntity<DeleteTranscriptionResponseDto> deleteTranscriptionById(@PathVariable Long transcriptionId) {
        try {
            transcriptionRespositoryPort.deleteById(transcriptionId);
            return ResponseEntity.status(200).body(
                    DeleteTranscriptionResponseDto.builder()
                    .status(Status.SUCCESS)
                    .message("Transcription deleted successfully.")
                    .build());
        } catch (Exception e) {
            log.error("Error deleting transcription with id {}: {}", transcriptionId, e.getMessage());
            return ResponseEntity.status(500).body(
                    DeleteTranscriptionResponseDto.builder()
                    .status(Status.FAILED)
                    .message("Failed to delete transcription.")
                    .build());
        }
    }
}
