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
    public void save(final Transcription transcription) {
        TranscriptionEntity entity = transcriptionMapper.toEntity(transcription);
        transcriptionJpaRepository.save(entity);
    }

    @Override
    public void deleteBydId(final Long id) {}

    @Override
    public Optional<Transcription> findByKeycloakId(final String keycloakId) {
        return transcriptionJpaRepository
            .findByKeycloakId(keycloakId)
            .map(transcriptionMapper::toDomain);
    }
}