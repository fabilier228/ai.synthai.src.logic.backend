package ai.synthai.businessbackend.application.service;

import ai.synthai.businessbackend.domain.model.analysis.EmotionalTranscriptionAnalysis;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

@Service
public class EmotionalTranscriptionService {
    public EmotionalTranscriptionAnalysis analyzeEmotion(MultipartFile audioFile) {
        String transcription = "";
        String emotion = "uncertain";
        java.io.File tempFile = null;
        try {
            // Save audio to temp file
            tempFile = java.io.File.createTempFile("audio", ".wav");
            audioFile.transferTo(tempFile);

            // Call Python model with audio file path
            ProcessBuilder pb = new ProcessBuilder(
                "python3", "src/main/resources/model-ai/predict.py", tempFile.getAbsolutePath()
            );
            pb.redirectErrorStream(true);
            Process process = pb.start();

            // Read the result from stdout
            try (java.io.BufferedReader reader = new java.io.BufferedReader(new java.io.InputStreamReader(process.getInputStream()))) {
                String line;
                StringBuilder output = new StringBuilder();
                while ((line = reader.readLine()) != null) {
                    output.append(line);
                }
                emotion = output.toString().trim();
            }

            process.waitFor();
        } catch (Exception e) {
            e.printStackTrace();
            emotion = "error";
        } finally {
            if (tempFile != null && tempFile.exists()) {
                tempFile.delete();
            }
        }

        return EmotionalTranscriptionAnalysis.builder()
            .transcription(transcription)
            .emotion(emotion)
            .build();
    }
}
