package ai.synthai.businessbackend.infrastructure.persistence;

import ai.synthai.businessbackend.infrastructure.persistence.entity.TranscriptionEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;


@Repository
public interface TranscriptionJpaRepository extends JpaRepository<TranscriptionEntity, Long> {
    Optional<TranscriptionEntity> findByKeycloakId(String keycloakId);
}
