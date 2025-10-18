package ai.synthai.businessbackend.infrastructure.persistence;

import ai.synthai.businessbackend.domain.model.Transcription;
import ai.synthai.businessbackend.domain.port.outbound.TranscriptionRespositoryPort;
import ai.synthai.businessbackend.infrastructure.persistence.entity.TranscriptionEntity;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.Optional;

@Component
@RequiredArgsConstructor
public class TranscriptionRepositoryFacade implements TranscriptionRespositoryPort {

    private final TranscriptionJpaRepository transcriptionJpaRepository;
    private final TranscriptionMapper transcriptionMapper;

    @Override
    public void save(TranscriptionEntity transcription) {
        transcriptionJpaRepository.save(transcription);
    }

    @Override
    public void deleteBydId(Long id) {

    }

    @Override
    public Optional<Transcription> findByKeycloakId(String keycloakId) {
        return transcriptionJpaRepository.findByKeycloakId(keycloakId).map(transcriptionMapper::toDomain);
    }
}