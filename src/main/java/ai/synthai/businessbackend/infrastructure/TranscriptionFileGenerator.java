package ai.synthai.businessbackend.infrastructure;

import ai.synthai.businessbackend.domain.model.Language;
import ai.synthai.businessbackend.domain.model.Transcription;
import ai.synthai.businessbackend.domain.model.analysis.summary.*;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.lowagie.text.*;
import com.lowagie.text.pdf.BaseFont;
import com.lowagie.text.pdf.PdfPCell;
import com.lowagie.text.pdf.PdfPTable;
import com.lowagie.text.pdf.PdfWriter;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.awt.Color;
import java.io.ByteArrayOutputStream;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

@Slf4j
@Component
@RequiredArgsConstructor
public class TranscriptionFileGenerator {

    private final ObjectMapper objectMapper;

    private static final Map<String, String> POLISH_FIELD_TRANSLATIONS = new HashMap<>();

    static {
        POLISH_FIELD_TRANSLATIONS.put("title", "Tytuł");
        POLISH_FIELD_TRANSLATIONS.put("language", "Język");
        POLISH_FIELD_TRANSLATIONS.put("tone", "Ton");
        POLISH_FIELD_TRANSLATIONS.put("summary", "Podsumowanie");
        POLISH_FIELD_TRANSLATIONS.put("emotions", "Emocje");
        POLISH_FIELD_TRANSLATIONS.put("keyQuotes", "Kluczowe cytaty");

        POLISH_FIELD_TRANSLATIONS.put("author", "Autor");
        POLISH_FIELD_TRANSLATIONS.put("narrator", "Narrator");
        POLISH_FIELD_TRANSLATIONS.put("genre", "Gatunek");
        POLISH_FIELD_TRANSLATIONS.put("subGenres", "Podgatunki");
        POLISH_FIELD_TRANSLATIONS.put("themes", "Motywy");
        POLISH_FIELD_TRANSLATIONS.put("narrativeStyle", "Styl narracji");
        POLISH_FIELD_TRANSLATIONS.put("setting", "Miejsce akcji");
        POLISH_FIELD_TRANSLATIONS.put("mainCharacters", "Główni bohaterowie");
        POLISH_FIELD_TRANSLATIONS.put("plotSummary", "Streszczenie fabuły");
        POLISH_FIELD_TRANSLATIONS.put("keyMoments", "Kluczowe momenty");
        POLISH_FIELD_TRANSLATIONS.put("symbolism", "Symbolika");
        POLISH_FIELD_TRANSLATIONS.put("pacing", "Tempo");
        POLISH_FIELD_TRANSLATIONS.put("audioStyle", "Styl audio");
        POLISH_FIELD_TRANSLATIONS.put("soundDesign", "Oprawa dźwiękowa");
        POLISH_FIELD_TRANSLATIONS.put("targetAudience", "Grupa docelowa");
        POLISH_FIELD_TRANSLATIONS.put("purpose", "Cel");
        POLISH_FIELD_TRANSLATIONS.put("complexityLevel", "Poziom złożoności");
        POLISH_FIELD_TRANSLATIONS.put("moodShifts", "Zmiany nastroju");
        POLISH_FIELD_TRANSLATIONS.put("narrativeArc", "Łuk narracyjny");

        POLISH_FIELD_TRANSLATIONS.put("participants", "Uczestnicy");
        POLISH_FIELD_TRANSLATIONS.put("relationship", "Relacja");
        POLISH_FIELD_TRANSLATIONS.put("context", "Kontekst");
        POLISH_FIELD_TRANSLATIONS.put("topics", "Tematy");
        POLISH_FIELD_TRANSLATIONS.put("conflictLevel", "Poziom konfliktu");
        POLISH_FIELD_TRANSLATIONS.put("agreementOutcome", "Wynik / Porozumienie");

        POLISH_FIELD_TRANSLATIONS.put("speaker", "Mówca");
        POLISH_FIELD_TRANSLATIONS.put("fieldOfStudy", "Dziedzina nauki");
        POLISH_FIELD_TRANSLATIONS.put("keyConcepts", "Kluczowe pojęcia");
        POLISH_FIELD_TRANSLATIONS.put("structure", "Struktura");
        POLISH_FIELD_TRANSLATIONS.put("mainArgument", "Główny argument");
        POLISH_FIELD_TRANSLATIONS.put("evidenceAndExamples", "Dowody i przykłady");
        POLISH_FIELD_TRANSLATIONS.put("conclusion", "Wnioski");

        POLISH_FIELD_TRANSLATIONS.put("artist", "Artysta");
        POLISH_FIELD_TRANSLATIONS.put("perspective", "Perspektywa");
        POLISH_FIELD_TRANSLATIONS.put("addressee", "Adresat");
        POLISH_FIELD_TRANSLATIONS.put("interpretation", "Interpretacja");
    }

    public byte[] generateTranscriptionFile(Transcription transcription, Language language) {
        try (ByteArrayOutputStream out = new ByteArrayOutputStream()) {
            Document document = new Document(PageSize.A4);
            PdfWriter.getInstance(document, out);

            document.open();

            Font titleFont = FontFactory.getFont(FontFactory.HELVETICA, BaseFont.CP1250, BaseFont.EMBEDDED, 18, Font.BOLD, Color.BLACK);
            Font headerFont = FontFactory.getFont(FontFactory.HELVETICA, BaseFont.CP1250, BaseFont.EMBEDDED, 12, Font.BOLD, Color.DARK_GRAY);
            Font bodyFont = FontFactory.getFont(FontFactory.HELVETICA, BaseFont.CP1250, BaseFont.EMBEDDED, 11, Font.NORMAL, Color.BLACK);

            String docTitle = transcription.getTitle() != null ? transcription.getTitle() : getLabel("untitled", language);
            Paragraph title = new Paragraph(docTitle, titleFont);
            title.setAlignment(Element.ALIGN_CENTER);
            title.setSpacingAfter(20);
            document.add(title);

            addSectionHeader(document, getLabel("transcript_header", language), headerFont);
            Paragraph transcriptPara = new Paragraph(transcription.getTranscript(), bodyFont);
            transcriptPara.setSpacingAfter(20);
            document.add(transcriptPara);

            String categoryLabel = transcription.getCategory();
            String analysisHeader = language == Language.POLISH
                    ? "Analiza i Podsumowanie (" + categoryLabel + ")"
                    : "Analysis & Summary (" + categoryLabel + ")";

            addSectionHeader(document, analysisHeader, headerFont);

            Map<String, Object> summaryData = parseSummaryToMap(transcription);

            if (summaryData != null && !summaryData.isEmpty()) {
                PdfPTable table = createSummaryTable(summaryData, headerFont, bodyFont, language);
                document.add(table);
            } else {
                document.add(new Paragraph(getLabel("no_data", language), bodyFont));
            }

            document.close();
            return out.toByteArray();

        } catch (Exception e) {
            log.error("Error generating PDF for transcription ID: {}", transcription.getId(), e);
            throw new RuntimeException("Failed to generate PDF file", e);
        }
    }

    private String getLabel(String key, Language language) {
        boolean isPl = (language == Language.POLISH);
        return switch (key) {
            case "untitled" -> isPl ? "Bez tytułu" : "Untitled";
            case "transcript_header" -> isPl ? "Transkrypcja" : "Transcript";
            case "no_data" -> isPl ? "Brak danych podsumowania." : "No summary data available.";
            case "table_feature" -> isPl ? "Cecha" : "Feature";
            case "table_value" -> isPl ? "Wartość" : "Value";
            default -> key;
        };
    }

    private void addSectionHeader(Document doc, String text, Font font) throws DocumentException {
        Paragraph p = new Paragraph(text, font);
        p.setSpacingBefore(10);
        p.setSpacingAfter(10);
        doc.add(p);
        doc.add(new com.lowagie.text.pdf.draw.LineSeparator());
        doc.add(new Paragraph(" "));
    }

    private Map<String, Object> parseSummaryToMap(Transcription transcription) {
        if (transcription.getSummary() == null || transcription.getCategory() == null) {
            return null;
        }

        try {
            Class<?> targetClass = switch (transcription.getCategory().toLowerCase()) {
                case "audiobook" -> AudiobookSummary.class;
                case "conversation" -> ConversationSummary.class;
                case "lecture" -> LectureSummary.class;
                case "song" -> SongSummary.class;
                default -> null;
            };

            if (targetClass == null) return null;

            Object summaryObject = objectMapper.readValue(transcription.getSummary(), targetClass);
            return objectMapper.convertValue(summaryObject, new TypeReference<LinkedHashMap<String, Object>>() {});

        } catch (Exception e) {
            log.error("Error parsing summary JSON", e);
            return null;
        }
    }

    private PdfPTable createSummaryTable(Map<String, Object> data, Font headerFont, Font bodyFont, Language language) {
        PdfPTable table = new PdfPTable(2);
        table.setWidthPercentage(100);
        try {
            table.setWidths(new float[]{30f, 70f});
        } catch (DocumentException e) {
            e.printStackTrace();
        }
        table.setSpacingBefore(10f);

        addCell(table, getLabel("table_feature", language), headerFont, true);
        addCell(table, getLabel("table_value", language), headerFont, true);

        for (Map.Entry<String, Object> entry : data.entrySet()) {
            String key = formatKey(entry.getKey(), language);
            String value = formatValue(entry.getValue());

            if (value != null && !value.isEmpty()) {
                addCell(table, key, bodyFont, false);
                addCell(table, value, bodyFont, false);
            }
        }

        return table;
    }

    private void addCell(PdfPTable table, String text, Font font, boolean isHeader) {
        PdfPCell cell = new PdfPCell(new Phrase(text, font));
        cell.setPadding(5);
        if (isHeader) {
            cell.setBackgroundColor(new Color(220, 220, 220));
            cell.setHorizontalAlignment(Element.ALIGN_CENTER);
        }
        table.addCell(cell);
    }

    private String formatKey(String key, Language language) {
        if (key == null) return "";

        if (language == Language.POLISH) {
            String translated = POLISH_FIELD_TRANSLATIONS.get(key);
            if (translated != null) {
                return translated;
            }
        }

        String text = key.replaceAll("([a-z])([A-Z]+)", "$1 $2");
        return text.substring(0, 1).toUpperCase() + text.substring(1);
    }

    private String formatValue(Object value) {
        if (value == null) return "-";
        if (value instanceof List<?>) {
            return String.join(", ", (List<String>) value);
        }
        return value.toString();
    }
}