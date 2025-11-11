package ai.synthai.businessbackend.domain.port.outbound;

import ai.synthai.businessbackend.domain.model.Transcription;

import java.util.Optional;

public interface TranscriptionRespositoryPort {

    void save(Transcription transcription);

    void deleteBydId(Long id);
    Optional<Transcription> findByKeycloakId(String id);
}
