package ai.synthai.businessbackend.domain.model.analysis.summary;

import lombok.*;

import java.util.List;


@Builder
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class SongSummary {
    String title;
    String artist;
    String language;
    String genre;
    List<String> themes;
    String tone;
    String perspective;
    String addressee;
    String interpretation;
    List<String> emotions;
    List<String> symbolism;
}
