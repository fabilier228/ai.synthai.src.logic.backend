package ai.synthai.businessbackend.domain.port.outbound;

import ai.synthai.businessbackend.domain.model.Transcription;

import java.util.List;

public interface TranscriptionRespositoryPort {

    void save(Transcription transcription);

    void deleteById(Long id);
    List<Transcription> findByKeycloakId(String id);

    Transcription findById(Long id);
    List<Transcription> findAll();
}
