package ai.synthai.businessbackend.application.service;


import ai.synthai.businessbackend.domain.ByteArrayMultipartFile;
import ai.synthai.businessbackend.domain.model.Language;
import ai.synthai.businessbackend.domain.port.outbound.TranscriptionRespositoryPort;
import ai.synthai.businessbackend.infrastructure.TranscriptionFileGenerator;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import lombok.val;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

@Slf4j
@Service
@RequiredArgsConstructor
public class TranscriptionFileService {
    private final TranscriptionRespositoryPort transcriptionRepositoryPort;
    private final TranscriptionFileGenerator transcriptionFileGenerator;

    public MultipartFile createTranscriptionFile(Long transcriptionId, Language language) {
        try {
            val transcription = transcriptionRepositoryPort.findById(transcriptionId);
            if (transcription == null) {
                log.error("Transcription with id {} not found", transcriptionId);
                return null;
            }
            val pdfBytes = transcriptionFileGenerator.generateTranscriptionFile(transcription, language);
            val filename = language == Language.POLISH ?
                    "transkrypcja_" + transcription.getId() + ".pdf" :
                    "transcription_" + transcription.getId() + ".pdf";
            return new ByteArrayMultipartFile(
                    pdfBytes,
                    "file",
                    filename,
                    "application/pdf"
            );
        } catch (Exception e) {
            log.error("Error generating transcription file for id {}: {}", transcriptionId, e.getMessage());
            return null;
        }


    }
}
