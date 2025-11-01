package ai.synthai.businessbackend.infrastructure.client.openai;

import ai.synthai.businessbackend.domain.model.Category;

public class PromptTemplateProvider {
    public String templateByCategory(Category category, String transcript) {
        return switch (category) {
            case PHONE_CALL -> getConversationTemplate(transcript);
            case SONG -> getSongTemplate(transcript);
            case AUDIOBOOK -> getAudiobookTemplate(transcript);
            case LECTURE -> getLectureTemplate(transcript);
        };
    }

    private String getSongTemplate(String transcript) {
        return "You are an expert music analyst and software system.  \n" +
                "Analyze the following song lyrics and return ONLY a valid JSON object (no markdown, no code fences, no extra text).  \n" +
                "The JSON must follow this schema with clear data types that can be safely deserialized in Java (e.g. using Jackson or Gson).\n" +
                "Song lyrics:\n" +
                "\"\"\"\n" +
                transcript + "\n" +
                "\"\"\"\n" +
                "Return strictly valid JSON according to this schema:\n" +
                "{\n" +
                "  \"title\": string | null,            // Song title, if identifiable; otherwise null\n" +
                "  \"artist\": string | null,           // Artist or author, if identifiable; otherwise null\n" +
                "  \"language\": string,                // Language of the lyrics (e.g. \"English\", \"Spanish\")\n" +
                "  \"genre\": string,                   // Approximate musical genre (e.g. \"pop\", \"rock\", \"rap\")\n" +
                "  \"themes\": [string],                // List of main lyrical themes (e.g. [\"love\", \"freedom\", \"loss\"])\n" +
                "  \"tone\": string,                    // Emotional tone or mood (e.g. \"melancholic\", \"energetic\")\n" +
                "  \"perspective\": string,             // Narrative point of view (e.g. \"first person\", \"third person\")\n" +
                "  \"addressee\": string,               // Who the lyrics are addressed to (e.g. \"lover\", \"society\", \"self\")\n" +
                "  \"interpretation\": string,          // 1–3 sentence summary of the song’s meaning or message\n" +
                "  \"emotions\": [string],              // Dominant emotions conveyed (e.g. [\"sadness\", \"hope\"])\n" +
                "  \"symbolism\": [string]              // Key symbols and their interpretations\n" +
                "}\n" +
                "\n" +
                "Formatting rules:\n" +
                "- Use `null` (not the string \"null\") for missing values.\n" +
                "- Always return valid JSON — no comments or trailing commas.\n" +
                "- Ensure arrays are valid JSON arrays, even if empty.\n" +
                "- Do NOT include markdown, explanations, or text outside the JSON object.\n";
    }

    private String getConversationTemplate(String transcript) {
        return "You are a conversation analyst and software system.  \n" +
                "Analyze the following dialogue between two people and return ONLY a valid JSON object (no markdown, no code fences, no text before or after).  \n" +
                "The JSON must follow this schema with clear data types suitable for deserialization in Java\n" +
                "Conversation transcript:\n" +
                transcript + "\n" +
                "Return strictly valid JSON according to this schema:\n" +
                "{\n" +
                "  \"participants\": [string],                // Names or identifiers of the two speakers, in speaking order\n" +
                "  \"language\": string,                      // Language of the conversation (e.g. \"English\", \"Polish\")\n" +
                "  \"relationship\": string,                  // Relationship between speakers (e.g. \"friends\", \"colleagues\", \"family\")\n" +
                "  \"context\": string,                       // Context or setting of the conversation (e.g. \"at work\", \"at home\", \"online chat\")\n" +
                "  \"topics\": [string],                      // Main topics discussed (e.g. [\"vacation\", \"job change\"])\n" +
                "  \"tone\": string,                          // Overall tone of the conversation (e.g. \"friendly\", \"tense\", \"formal\")\n" +
                "  \"speakerRoles\": {                        // Roles or communication styles of each participant\n" +
                "    \"personA\": string,                     // e.g. \"dominant\", \"listener\", \"mediator\"\n" +
                "    \"personB\": string                      // e.g. \"passive\", \"assertive\", \"emotional\"\n" +
                "  },\n" +
                "  \"summary\": string,                       // Short summary (1–3 sentences) describing the flow and key points of the conversation\n" +
                "  \"emotions\": [string],                    // Dominant emotions expressed (e.g. [\"anger\", \"joy\", \"nervousness\"])\n" +
                "  \"conflictLevel\": string,                 // Qualitative level of disagreement (e.g. \"none\", \"mild\", \"moderate\", \"strong\")\n" +
                "  \"agreementOutcome\": string | null,       // Whether they reached an agreement or resolution; null if not applicable\n" +
                "  \"keyQuotes\": [string]                    // 2–4 short representative quotes from the conversation\n" +
                "}\n" +
                "Formatting rules:\n" +
                "- Use `null` (not the string \"null\") for missing or unknown values.\n" +
                "- Always return valid JSON — no comments or trailing commas.\n" +
                "- Arrays must always be valid JSON arrays (even if empty).\n" +
                "- Do NOT include markdown, explanations, or text outside the JSON object.\n";
    }

    private String getLectureTemplate(String transcript) {
        return "You are an academic content analyst and software system.  \n" +
                "Analyze the following lecture transcript and return ONLY a valid JSON object (no markdown, no code fences, no text before or after).  \n" +
                "The JSON must follow this schema with clear data types suitable for deserialization in Java \n" +
                "Lecture transcript:\n" +
                transcript + "\n" +
                "Return strictly valid JSON according to this schema:{\n" +
                "  \"title\": string | null,              // Title or main subject of the lecture, if identifiable; otherwise null\n" +
                "  \"speaker\": string | null,           // Name of the lecturer or professor, if available; otherwise null\n" +
                "  \"language\": string,                    // Language of the lecture (e.g. \"English\", \"Polish\")\n" +
                "  \"fieldOfStudy\": string,                // Academic discipline or field (e.g. \"physics\", \"history\", \"computer science\")\n" +
                "  \"topics\": [string],                    // Main topics covered (e.g. [\"quantum mechanics\", \"wave-particle duality\"])\n" +
                "  \"keyConcepts\": [string],               // Important terms, theories, or ideas introduced\n" +
                "  \"tone\": string,                        // General tone of delivery (e.g. \"formal\", \"enthusiastic\", \"technical\")\n" +
                "  \"structure\": [string],                 // Logical structure of the lecture (e.g. [\"introduction\", \"methods\", \"examples\", \"summary\"])\n" +
                "  \"targetAudience\": string,              // Intended audience (e.g. \"students\", \"researchers\", \"general public\")\n" +
                "  \"summary\": string,                     // Concise summary (2–4 sentences) describing the lecture's main points\n" +
                "  \"keyQuotes\": [string],                 // 2–4 notable statements or defining sentences from the lecture\n" +
                "  \"mainArgument\": string,                // Central thesis or argument presented\n" +
                "  \"evidenceAndExamples\": [string],       // Brief descriptions of examples or evidence used to support key points\n" +
                "  \"conclusion\": string | null,           // Summary of final conclusions, if present; otherwise null\n" +
                "  \"emotions\": [string],                  // Emotional tone or sentiment expressed (e.g. [\"inspiring\", \"neutral\", \"critical\"])\n" +
                "  \"complexityLevel\": string,             // Difficulty level (e.g. \"basic\", \"intermediate\", \"advanced\")\n" +
                "  \"purpose\": string,                     // Main purpose of the lecture (e.g. \"educate\", \"motivate\", \"explain theory\")\n" +
                "  \"durationMinutes\": number | null       // Approximate duration in minutes, if mentioned; otherwise null}\n" +
                "Formatting rules:\n" +
                "- Use `null` (not the string \"null\") for missing or unknown values.\n" +
                "- Always return valid JSON — no comments or trailing commas.\n" +
                "- Arrays must always be valid JSON arrays (even if empty).\n" +
                "- Do NOT include markdown, explanations, or text outside the JSON object.\n";
    }

    private String getAudiobookTemplate(String transcript) {
        return "You are a literary and audio content analyst and software system.  \n" +
                "Analyze the following audiobook transcript or description and return ONLY a valid JSON object (no markdown, no code fences, no text before or after).  \n" +
                "The JSON must follow this schema with clear data types suitable for deserialization in Java\n" +
                "Audiobook transcript or description:\n" +
                transcript + "\n" +
                "Return strictly valid JSON according to this schema:\n" +
                "{\n" +
                "  \"title\": string | null,                   // Title of the audiobook, if identifiable; otherwise null\n" +
                "  \"author\": string | null,                  // Author of the original text, if known; otherwise null\n" +
                "  \"narrator\": string | null,                // Name of the narrator, if mentioned; otherwise null\n" +
                "  \"language\": string,                       // Language of the audiobook (e.g. \"English\", \"Polish\")\n" +
                "  \"genre\": string,                          // Literary genre (e.g. \"fiction\", \"mystery\", \"self-help\", \"fantasy\")\n" +
                "  \"subGenres\": [string],                    // Subgenres or stylistic categories (e.g. [\"psychological thriller\", \"historical fiction\"])\n" +
                "  \"themes\": [string],                       // Main themes or ideas (e.g. [\"identity\", \"love\", \"betrayal\"])\n" +
                "  \"tone\": string,                           // Overall emotional tone (e.g. \"dramatic\", \"introspective\", \"dark\")\n" +
                "  \"narrativeStyle\": string,                 // Narrative perspective (e.g. \"first person\", \"third person omniscient\")\n" +
                "  \"setting\": string,                        // Primary setting or time period (e.g. \"Victorian London\", \"future Earth\")\n" +
                "  \"mainCharacters\": [string],               // List of main characters\n" +
                "  \"plotSummary\": string,                    // Concise summary (3–5 sentences) describing the main plot and key events\n" +
                "  \"keyMoments\": [string],                   // 2–4 pivotal or defining scenes/moments\n" +
                "  \"emotions\": [string],                     // Dominant emotions conveyed in the narration\n" +
                "  \"symbolism\": [string],                    // Major symbols or motifs and their meanings\n" +
                "  \"pacing\": string,                         // Narrative pacing (e.g. \"slow\", \"moderate\", \"fast\")\n" +
                "  \"audioStyle\": string,                     // Style of narration (e.g. \"calm\", \"expressive\", \"neutral\", \"theatrical\")\n" +
                "  \"soundDesign\": string | null,             // Description of sound design or background effects, if any\n" +
                "  \"targetAudience\": string,                 // Intended audience (e.g. \"young adults\", \"general readers\", \"professionals\")\n" +
                "  \"lengthMinutes\": number | null,           // Approximate duration in minutes, if known; otherwise null\n" +
                "  \"purpose\": string,                        // Main purpose (e.g. \"entertain\", \"educate\", \"inspire\", \"inform\")\n" +
                "  \"complexityLevel\": string,                // Comprehension or conceptual difficulty (e.g. \"basic\", \"intermediate\", \"advanced\"),\n" +
                "  \"moodShifts\": [string],                   // Notable emotional or tonal shifts throughout the audiobook\n" +
                "  \"narrativeArc\": [string]                  // High-level structure (e.g. [\"introduction\", \"conflict\", \"climax\", \"resolution\"])\n" +
                "}\n" +
                "\n" +
                "Formatting rules:\n" +
                "- Use `null` (not the string \"null\") for missing or unknown values.\n" +
                "- Always return valid JSON — no comments or trailing commas.\n" +
                "- Arrays must always be valid JSON arrays (even if empty).\n" +
                "- Do NOT include markdown, explanations, or text outside the JSON object.\n";
    }


}
