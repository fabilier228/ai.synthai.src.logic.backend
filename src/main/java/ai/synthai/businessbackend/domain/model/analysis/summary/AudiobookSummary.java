package ai.synthai.businessbackend.domain.model.analysis.summary;

import lombok.Builder;
import lombok.Getter;

import java.util.List;

@Builder
@Getter
public class AudiobookSummary {
    private String title;
    private String author;
    private String narrator;
    private String language;
    private String genre;
    private List<String> subGenres;
    private List<String> themes;
    private String tone;
    private String narrativeStyle;
    private String setting;
    private List<String> mainCharacters;
    private String plotSummary;
    private List<String> keyMoments;
    private List<String> emotions;
    private List<String> symbolism;
    private String pacing;
    private String audioStyle;
    private String soundDesign;
    private String targetAudience;
    private Integer lengthMinutes;
    private String purpose;
    private String complexityLevel;
    private List<String> moodShifts;
    private List<String> narrativeArc;
}
