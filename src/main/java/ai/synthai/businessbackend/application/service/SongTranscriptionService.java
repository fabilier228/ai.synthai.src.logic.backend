package ai.synthai.businessbackend.application.service;

import ai.synthai.businessbackend.application.dto.TranscriptionResultDto;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;


import java.io.File;

@Component
@RequiredArgsConstructor
public class SongTranscriptionService {

    public TranscriptionResultDto songFlow(File audioFile) {
        return null;
    }

}
