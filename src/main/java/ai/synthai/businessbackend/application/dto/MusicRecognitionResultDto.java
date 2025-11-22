package ai.synthai.businessbackend.application.dto;

import lombok.Builder;
import lombok.Getter;

@Builder
@Getter
public class MusicRecognitionResultDto {
    private final String title;
    private final String artist;
    private final String album;
    private final String label;
    private final String releaseDate;
    private final Integer duration;
    private final String genre;
    private final Double confidence;
    private final boolean recognized;
}