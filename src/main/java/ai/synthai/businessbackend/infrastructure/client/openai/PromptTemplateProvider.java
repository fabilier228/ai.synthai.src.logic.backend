package ai.synthai.businessbackend.infrastructure.client.openai;

import ai.synthai.businessbackend.domain.model.Category;
import org.springframework.stereotype.Component;
@Component
public class PromptTemplateProvider {
    private static final String outputLanguageInstruction = """
        IMPORTANT LANGUAGE RULES:
        1. Detect the language of the input transcript.
        2. The VALUES inside the JSON must be in the same language as the transcript (e.g., if input is Polish, summary and analysis must be in Polish).
        3. The KEYS of the JSON (e.g., "title", "summary", "tone") MUST remain in English exactly as defined in the schema.
        4. Do not translate the structure, only the content.
        """;
    public String templateByCategory(Category category, String transcript) {
        return switch (category) {
            case CONVERSATION -> getConversationTemplate(transcript);
            case SONG -> getSongTemplate(transcript);
            case AUDIOBOOK -> getAudiobookTemplate(transcript);
            case LECTURE -> getLectureTemplate(transcript);
        };
    }

    private String getSongTemplate(String transcript) {
        return """
                You are an expert music analyst and software system.
                Analyze the following song lyrics and return ONLY a valid JSON object (no markdown,
                no code fences, no extra text).
                The JSON must follow this schema with clear data types that can be safely
                deserialized in Java (e.g. using Jackson or Gson).
                Song lyrics:
                \"\"\"
                %s
                \"\"\"
                Return strictly valid JSON according to this schema:
                {
                  "title": string | null,     // Song title, if identifiable; otherwise null
                  "artist": string | null,    // Artist or author, if identifiable; otherwise null
                  "language": string,               // Language of the lyrics (e.g. "English", "Spanish")
                  "genre": string,                   // Approximate musical genre (e.g. "pop", "rock", "rap")
                  "themes": [string],                // List of main lyrical themes (e.g. ["love", "freedom", "loss"])
                  "tone": string,                    // Emotional tone or mood (e.g. "melancholic", "energetic")
                  "perspective": string,             // Narrative point of view (e.g. "first person", "third person")
                  "addressee": string,               // Who the lyrics are addressed to (e.g. "lover", "society", "self")
                  "interpretation": string,          // 1–3 sentence summary of the song’s meaning or message
                  "emotions": [string],              // Dominant emotions conveyed (e.g. ["sadness", "hope"])
                  "symbolism": [string]
                }

                Formatting rules:
                - Use `null` (not the string "null") for missing values.
                - Always return valid JSON — no comments or trailing commas.
                - Ensure arrays are valid JSON arrays, even if empty.
                - Do NOT include markdown, explanations, or text outside the JSON object.
                
                %s
                """.formatted(transcript, outputLanguageInstruction);
    }

    private String getConversationTemplate(String transcript) {
        return """
                You are a conversation analyst and software system.
                Analyze the following dialogue between two people and return ONLY a valid JSON object (no markdown,
                no code fences, no text before or after).
                The JSON must follow this schema with clear data types suitable for deserialization in Java
                Conversation transcript:
                %s
                Return strictly valid JSON according to this schema:
                {
                  "participants": [string],                // Names or identifiers of the two speakers, in speaking order
                  "language": string,                      // Language of the conversation (e.g. "English", "Polish")
                  "relationship": string,                  // Relationship between speakers (e.g. "friends", "colleagues", "family")
                  "context": string,                       // Context or setting of the conversation (e.g. "at work", "at home", "online chat")
                  "topics": [string],                      // Main topics discussed (e.g. ["vacation", "job change"])
                  "tone": string,                          // Overall tone of the conversation (e.g. "friendly", "tense", "formal")
                  "summary": string,                       // Short summary (1–3 sentences) describing the flow and key points of the conversation
                  "emotions": [string],                    // Dominant emotions expressed (e.g. ["anger", "joy", "nervousness"])
                  "conflictLevel": string,                 // Qualitative level of disagreement (e.g. "none", "mild", "moderate", "strong")
                  "agreementOutcome": string | null,       // Whether they reached an agreement or resolution; null if not applicable
                  "keyQuotes": [string]                    // 2–4 short representative quotes from the conversation
                }
                Formatting rules:
                - Use `null` (not the string "null") for missing or unknown values.
                - Always return valid JSON — no comments or trailing commas.
                - Arrays must always be valid JSON arrays (even if empty).
                - Do NOT include markdown, explanations, or text outside the JSON object.
                
                %s
                """.formatted(transcript, outputLanguageInstruction);
    }

    private String getLectureTemplate(String transcript) {
        return """
                You are an academic content analyst and software system.
                Analyze the following lecture transcript and return ONLY a valid JSON object (no markdown,
                no code fences, no text before or after).
                The JSON must follow this schema with clear data types suitable for deserialization in Java
                Lecture transcript:
                %s
                Return strictly valid JSON according to this schema:
                {
                  "title": string | null,              // Title or main subject of the lecture, if identifiable; otherwise null
                  "speaker": string | null,           // Name of the lecturer or professor, if available; otherwise null
                  "language": string,                    // Language of the lecture (e.g. "English", "Polish")
                  "fieldOfStudy": string,                // Academic discipline or field (e.g. "physics", "history", "computer science")
                  "topics": [string],                    // Main topics covered (e.g. ["quantum mechanics", "wave-particle duality"])
                  "keyConcepts": [string],               // Important terms, theories, or ideas introduced
                  "tone": string,                        // General tone of delivery (e.g. "formal", "enthusiastic", "technical")
                  "structure": [string],                 // Logical structure of the lecture (e.g. ["introduction", "methods", "examples", "summary"])
                  "targetAudience": string,              // Intended audience (e.g. "students", "researchers", "general public")
                  "summary": string,                     // Concise summary (2–4 sentences) describing the lecture's main points
                  "keyQuotes": [string],                 // 2–4 notable statements or defining sentences from the lecture
                  "mainArgument": string,                // Central thesis or argument presented
                  "evidenceAndExamples": [string],       // Brief descriptions of examples or evidence used to support key points
                  "conclusion": string | null,           // Summary of final conclusions, if present; otherwise null
                  "emotions": [string],                  // Emotional tone or sentiment expressed (e.g. ["inspiring", "neutral", "critical"])
                  "complexityLevel": string,             // Difficulty level (e.g. "basic", "intermediate", "advanced")
                  "purpose": string,                     // Main purpose of the lecture (e.g. "educate", "motivate", "explain theory")
                }
                Formatting rules:
                - Use `null` (not the string "null") for missing or unknown values.
                - Always return valid JSON — no comments or trailing commas.
                - Arrays must always be valid JSON arrays (even if empty).
                - Do NOT include markdown, explanations, or text outside the JSON object.
                
                %s
                """.formatted(transcript, outputLanguageInstruction);
    }

    private String getAudiobookTemplate(String transcript) {
        return """
                You are a literary and audio content analyst and software system.
                Analyze the following audiobook transcript or description and return ONLY a valid JSON object (no markdown,
                no code fences, no text before or after).
                The JSON must follow this schema with clear data types suitable for deserialization in Java
                Audiobook transcript or description:
                %s
                Return strictly valid JSON according to this schema:
                {
                  "title": string | null,                   // Title of the audiobook, if identifiable; otherwise null
                  "author": string | null,                  // Author of the original text, if known; otherwise null
                  "narrator": string | null,                // Name of the narrator, if mentioned; otherwise null
                  "language": string,                       // Language of the audiobook (e.g. "English", "Polish")
                  "genre": string,                          // Literary genre (e.g. "fiction", "mystery", "self-help", "fantasy")
                  "subGenres": [string],                    // Subgenres or stylistic categories (e.g. ["psychological thriller", "historical fiction"])
                  "themes": [string],                       // Main themes or ideas (e.g. ["identity", "love", "betrayal"])
                  "tone": string,                           // Overall emotional tone (e.g. "dramatic", "introspective", "dark")
                  "narrativeStyle": string,                 // Narrative perspective (e.g. "first person", "third person omniscient")
                  "setting": string,                        // Primary setting or time period (e.g. "Victorian London", "future Earth")
                  "mainCharacters": [string],               // List of main characters
                  "plotSummary": string,                    // Concise summary (3–5 sentences) describing the main plot and key events
                  "keyMoments": [string],                   // 2–4 pivotal or defining scenes/moments
                  "emotions": [string],                     // Dominant emotions conveyed in the narration
                  "symbolism": [string],                    // Major symbols or motifs and their meanings
                  "pacing": string,                         // Narrative pacing (e.g. "slow", "moderate", "fast")
                  "targetAudience": string,                 // Intended audience (e.g. "young adults", "general readers", "professionals")
                  "purpose": string,                        // Main purpose (e.g. "entertain", "educate", "inspire", "inform")
                  "complexityLevel": string,                // Comprehension or conceptual difficulty (e.g. "basic", "intermediate", "advanced"),
                  "moodShifts": [string],                   // Notable emotional or tonal shifts throughout the audiobook
                  "narrativeArc": [string]                  // High-level structure (e.g. ["introduction", "conflict", "climax", "resolution"])
                }

                Formatting rules:
                - Use `null` (not the string "null") for missing or unknown values.
                - Always return valid JSON — no comments or trailing commas.
                - Arrays must always be valid JSON arrays (even if empty).
                - Do NOT include markdown, explanations, or text outside the JSON object.
                
                %s
                """.formatted(transcript, outputLanguageInstruction);
    }
}