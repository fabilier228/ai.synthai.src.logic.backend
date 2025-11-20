package ai.synthai.businessbackend.infrastructure.persistence;

import ai.synthai.businessbackend.infrastructure.persistence.entity.TranscriptionEntity;
import org.jetbrains.annotations.NotNull;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;



@Repository
public interface TranscriptionJpaRepository extends JpaRepository<TranscriptionEntity, Long> {
    List<TranscriptionEntity> findByKeycloakId(
        String keycloakId
    );

    @NotNull
    TranscriptionEntity findTranscriptionEntityById(
        Long id
    );
    @NotNull
    @Override
    List<TranscriptionEntity> findAll();
}
