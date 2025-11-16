package ai.synthai.businessbackend.infrastructure.persistence;

import ai.synthai.businessbackend.domain.model.Transcription;
import ai.synthai.businessbackend.domain.port.outbound.TranscriptionRespositoryPort;
import ai.synthai.businessbackend.infrastructure.persistence.entity.TranscriptionEntity;
import lombok.RequiredArgsConstructor;
import lombok.val;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
@RequiredArgsConstructor
public class TranscriptionRepositoryFacade implements TranscriptionRespositoryPort {

    private final TranscriptionJpaRepository transcriptionJpaRepository;

    @Override
    public void save(final Transcription transcription) {
        TranscriptionEntity entity = TranscriptionMapper.toEntity(transcription);
        transcriptionJpaRepository.save(entity);
    }

    @Override
    public void deleteById(final Long id) {}

    @Override
    public List<Transcription> findByKeycloakId(final String keycloakId) {
        val entities =  transcriptionJpaRepository
            .findByKeycloakId(keycloakId);

        return entities.stream()
            .map(TranscriptionMapper::toDomain)
            .toList();
    }

    @Override
    public List<Transcription> findAll() {
        val entities = transcriptionJpaRepository.findAll();
        return entities.stream()
            .map(TranscriptionMapper::toDomain)
            .toList();
    }
}