package ai.synthai.businessbackend.infrastructure;

import lombok.extern.slf4j.Slf4j;
import lombok.val;
import org.springframework.stereotype.Component;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;

@Slf4j
@Component
public class EmotionDetector {
    public String detectEmotion(String text) {

        String emotion;
        try {
            val pb = new ProcessBuilder(
                    "python",
                    "src/main/resources/model-ai/predict.py",
                    text
            );

            pb.redirectErrorStream(true);
            val process = pb.start();

            try (BufferedReader reader = new BufferedReader(
                    new InputStreamReader(process.getInputStream(), StandardCharsets.UTF_8))) {

                String line;
                StringBuilder output = new StringBuilder();
                while ((line = reader.readLine()) != null) {
                    output.append(line);
                }
                emotion = output.toString().trim();
            }

            int exitCode = process.waitFor();
            if (exitCode != 0) {
                log.error("Python script exited with code: " + exitCode + ". Output: " + emotion);
                emotion = "error";
            }

        } catch (Exception e) {
            e.printStackTrace();
            emotion = "error";
        }

        return emotion;
    }
}
