package ai.synthai.businessbackend.domain.model.analysis.summary;

import lombok.Builder;
import lombok.Getter;

import java.util.List;


@Builder
@Getter
public class SongSummary {
    String title;
    String artist;
    String language;
    String genre;
    List<String> genres;
    String tone;
    String perspective;
    String adressee;
    String interpretation;
    List<String> emotions;
    List<String> symbolism;
}
