package ai.synthai.businessbackend.infrastructure.controller;

import ai.synthai.businessbackend.domain.model.MusicRecognitionResult;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import ai.synthai.businessbackend.application.service.SongService;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

@RestController
@RequestMapping("/songs")
@RequiredArgsConstructor
@Slf4j
public class SongController {


    private final SongService songService;

    @PostMapping(value = "/analyze", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)

    public ResponseEntity<MusicRecognitionResult> analyzeSong(@RequestParam("audioFile") MultipartFile audioFile) {
        log.info("Received song analysis request for file: {}", audioFile.getOriginalFilename());
        if (audioFile.isEmpty()) {
            return ResponseEntity.badRequest().build();
        }
        MusicRecognitionResult result = songService.analyzeSong(audioFile);
        if (result.isRecognized()) {
            return ResponseEntity.ok(result);
        } else {
            return ResponseEntity.internalServerError().build();
        }
    }

    @GetMapping("/test")
    public ResponseEntity<String> test() {
        return ResponseEntity.ok("Song analysis service is running!");
    }
}
