package ai.synthai.businessbackend.infrastructure.rest;

import ai.synthai.businessbackend.domain.model.Transcription;
import ai.synthai.businessbackend.domain.port.outbound.TranscriptionRespositoryPort;
import lombok.AllArgsConstructor;
import lombok.val;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

import java.util.List;

@Controller
@AllArgsConstructor
@RequestMapping("test")
public class TestController {

    private final TranscriptionRespositoryPort transcriptionRespositoryPort;

    @GetMapping("/get-all")
    public ResponseEntity<List<Transcription>> testAnalysis() {
        val entities = transcriptionRespositoryPort.findAll();
        return ResponseEntity.status(200).body(entities);
    }

}
