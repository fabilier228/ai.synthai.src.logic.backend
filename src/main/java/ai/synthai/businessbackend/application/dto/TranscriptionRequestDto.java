package ai.synthai.businessbackend.application.dto;

import ai.synthai.businessbackend.domain.model.Language;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

public record TranscriptionRequestDto(
         @RequestParam("audioFile") MultipartFile audioFile,
         @RequestParam(value = "language", required = false, defaultValue = "EN") Language language,
         @RequestParam("keycloakId") String keycloakId,
         @RequestParam("title") String title,
         @RequestParam(value = "temperature", required = false, defaultValue = "0.7") double temperature,
         @RequestParam(value = "diarization", required = false, defaultValue = "true") boolean diarization,
         @RequestParam(value = "phraseList", required = false) List<String> phraseList
)  {

}
